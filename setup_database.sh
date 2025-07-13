#!/bin/bash

echo "🚀 Setting up E-Shop Console Database..."
echo ""

# Check if PostgreSQL is running
if ! pg_isready -h localhost -p 5432 > /dev/null 2>&1; then
    echo "❌ PostgreSQL is not running. Please start PostgreSQL first."
    exit 1
fi

echo "✅ PostgreSQL is running"
echo ""

# Run the setup script
echo "📝 Running database setup script..."
psql -U postgres -f src/main/resources/setup_database.sql

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Database setup completed successfully!"
    echo ""
    echo "📊 Database details:"
    echo "   Database: shopease"
    echo "   User: eshop"
    echo "   Schema: eshop"
    echo ""
    echo "🔗 Connection URL: jdbc:postgresql://localhost:5432/shopease"
    echo ""
    echo "🎉 You can now run your Java application!"
else
    echo ""
    echo "❌ Database setup failed. Please check the error messages above."
    exit 1
fi 