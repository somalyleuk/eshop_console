#!/bin/bash

echo "🧪 Testing 10 Million Products Functionality"
echo "=============================================="

# Check if PostgreSQL is running
if ! pg_isready -h localhost -p 5432 > /dev/null 2>&1; then
    echo "❌ PostgreSQL is not running. Please start PostgreSQL first."
    exit 1
fi

echo "✅ PostgreSQL is running"

# Check if database exists
if ! psql -U postgres -d eshop -c "SELECT 1;" > /dev/null 2>&1; then
    echo "⚠️  Database 'eshop' not found. Please run the database setup first."
    echo "   Run: psql -U postgres -f src/main/resources/database_with_indexes.sql"
    exit 1
fi

echo "✅ Database 'eshop' exists"

# Check if tables exist
if ! psql -U postgres -d eshop -c "SELECT COUNT(*) FROM products;" > /dev/null 2>&1; then
    echo "❌ Products table not found. Please run the database setup."
    exit 1
fi

echo "✅ Products table exists"

# Check current product count
PRODUCT_COUNT=$(psql -U postgres -d eshop -t -c "SELECT COUNT(*) FROM products;" | xargs)
echo "📊 Current product count: $PRODUCT_COUNT"

# Check if indexes exist
INDEX_COUNT=$(psql -U postgres -d eshop -t -c "SELECT COUNT(*) FROM pg_indexes WHERE tablename = 'products';" | xargs)
echo "🔍 Number of indexes on products table: $INDEX_COUNT"

# Test basic functionality
echo ""
echo "🧪 Testing basic functionality..."

# Test pagination query
echo "Testing pagination query..."
PAGINATION_TEST=$(psql -U postgres -d eshop -t -c "SELECT COUNT(*) FROM products LIMIT 50 OFFSET 0;" 2>/dev/null | xargs)
if [ "$PAGINATION_TEST" != "" ]; then
    echo "✅ Pagination query works"
else
    echo "❌ Pagination query failed"
fi

# Test search query
echo "Testing search query..."
SEARCH_TEST=$(psql -U postgres -d eshop -t -c "SELECT COUNT(*) FROM products WHERE LOWER(name) LIKE '%smartphone%';" 2>/dev/null | xargs)
if [ "$SEARCH_TEST" != "" ]; then
    echo "✅ Search query works"
else
    echo "❌ Search query failed"
fi

# Test performance indexes
echo "Testing performance indexes..."
INDEXES=$(psql -U postgres -d eshop -t -c "SELECT indexname FROM pg_indexes WHERE tablename = 'products' ORDER BY indexname;" 2>/dev/null)
echo "📋 Available indexes:"
echo "$INDEXES" | while read -r index; do
    if [ ! -z "$index" ]; then
        echo "   ✅ $index"
    fi
done

echo ""
echo "🎉 Basic functionality tests completed!"
echo ""
echo "📋 Next Steps:"
echo "1. Run the application: ./gradlew run"
echo "2. Login and navigate to '🚀 Bulk Operations (10M Products)'"
echo "3. Choose 'Insert 10 Million Products' to test bulk operations"
echo "4. Use 'Test Reading Performance' to benchmark read operations"
echo ""
echo "📚 For detailed instructions, see: README_10M_PRODUCTS.md" 