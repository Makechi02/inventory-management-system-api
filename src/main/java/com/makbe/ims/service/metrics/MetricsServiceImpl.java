package com.makbe.ims.service.metrics;

import com.makbe.ims.collections.Item;
import com.makbe.ims.dto.category.CategoryDtoMapper;
import com.makbe.ims.dto.category.ModelCategoryDto;
import com.makbe.ims.dto.item.ItemDto;
import com.makbe.ims.dto.item.ItemDtoMapper;
import com.makbe.ims.dto.metrics.MetricsDto;
import com.makbe.ims.dto.metrics.StockLevelDto;
import com.makbe.ims.dto.supplier.ModelSupplierDto;
import com.makbe.ims.dto.supplier.SupplierDtoMapper;
import com.makbe.ims.dto.user.ModelUserDto;
import com.makbe.ims.dto.user.UserMapper;
import com.makbe.ims.repository.CategoryRepository;
import com.makbe.ims.repository.ItemRepository;
import com.makbe.ims.repository.SupplierRepository;
import com.makbe.ims.repository.UserRepository;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MetricsServiceImpl implements MetricService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;

    private final UserMapper userMapper;
    private final CategoryDtoMapper categoryDtoMapper;
    private final SupplierDtoMapper supplierDtoMapper;
    private final ItemDtoMapper itemDtoMapper;

    private final MongoTemplate mongoTemplate;

    @Override
    public MetricsDto getAllMetrics() {
        long totalUsers = userRepository.count();
        long totalItems = itemRepository.count();
        long totalCategories = categoryRepository.count();
        long totalSuppliers = supplierRepository.count();

        List<ModelUserDto> recentUsers = userRepository.findTop5ByOrderByCreatedAtDesc().stream()
                .map(userMapper::toModelUserDto).toList();

        List<ItemDto> recentItems = itemRepository.findTop5ByOrderByCreatedAtDesc().stream().map(itemDtoMapper).toList();
        List<ModelCategoryDto> recentCategories = categoryRepository.findTop5ByOrderByCreatedAtDesc().stream()
                .map(categoryDtoMapper::toModelCategoryDto).toList();
        List<ModelSupplierDto> recentSuppliers = supplierRepository.findTop5ByOrderByAddedAtDesc().stream()
                .map(supplierDtoMapper::toModelSupplierDto).toList();

        return MetricsDto.builder()
                .totalUsers(totalUsers)
                .totalItems(totalItems)
                .totalCategories(totalCategories)
                .totalSuppliers(totalSuppliers)
                .recentUsers(recentUsers)
                .recentItems(recentItems)
                .recentCategories(recentCategories)
                .recentSuppliers(recentSuppliers)
                .build();
    }

    @Override
    public double calculateInventoryValuation() {
        List<Item> items = itemRepository.findAllForInventoryValuation();
        return items.stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();
    }

    @Override
    public List<StockLevelDto> getStockLevels() {
        List<Item> items = itemRepository.findAllForStockLevels();
        return items.stream()
                .map(item -> StockLevelDto.builder()
                        .name(item.getName())
                        .quantity(item.getQuantity())
                        .stockAlert(item.getStockAlert())
                        .build())
                .toList();
    }

    @Override
    public List<Document> getItemsGroupedByCategory() {
        MongoDatabase database = mongoTemplate.getDb();
        MongoCollection<Document> collection = database.getCollection("items");

        List<Document> aggregationPipeline = Arrays.asList(
                new Document("$group", new Document("_id", "$category")
                        .append("count", new Document("$sum", 1))),
                new Document("$lookup", new Document("from", "categories")
                        .append("localField", "_id")
                        .append("foreignField", "_id")
                        .append("as", "category")),
                new Document("$unwind", new Document("path", "$category")
                        .append("preserveNullAndEmptyArrays", true)),
                new Document("$project", new Document("_id", 1)
                        .append("count", 1)
                        .append("categoryName", new Document("$ifNull", Arrays.asList("$category.name", "Unknown Category"))))
        );

        AggregateIterable<Document> results = collection.aggregate(aggregationPipeline);
        return results.into(new ArrayList<>());
    }
}
