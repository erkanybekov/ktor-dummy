#!/bin/bash

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_color() {
    color=$1
    text=$2
    echo -e "${color}${text}${NC}"
}

show_help() {
    echo "🚀 Ktor Todo Application Service Manager"
    echo ""
    echo "Usage: $0 [command]"
    echo ""
    echo "Commands:"
    echo "  start-all      Start all services"
    echo "  stop-all       Stop all services"
    echo "  restart-all    Restart all services"
    echo "  start-core     Start core services (postgres, redis, app)"
    echo "  start-monitoring Start monitoring services (prometheus, grafana)"
    echo "  start-logging  Start logging services (elasticsearch, kibana, logstash)"
    echo "  start-admin    Start admin interfaces (pgadmin, redis-commander, portainer)"
    echo "  status         Show status of all services"
    echo "  logs           Show logs for all services"
    echo "  logs-app       Show application logs"
    echo "  clean          Clean up all containers and volumes"
    echo "  build          Build application image"
    echo "  test           Test all services"
    echo "  help           Show this help"
}

start_all() {
    print_color $GREEN "🚀 Starting all services..."
    docker-compose up -d
    print_color $GREEN "✅ All services started!"
    show_urls
}

stop_all() {
    print_color $YELLOW "🛑 Stopping all services..."
    docker-compose down
    print_color $GREEN "✅ All services stopped!"
}

restart_all() {
    print_color $YELLOW "🔄 Restarting all services..."
    docker-compose down
    docker-compose up -d
    print_color $GREEN "✅ All services restarted!"
    show_urls
}

start_core() {
    print_color $GREEN "🔧 Starting core services..."
    docker-compose up -d postgres redis app nginx
    print_color $GREEN "✅ Core services started!"
}

start_monitoring() {
    print_color $GREEN "📊 Starting monitoring services..."
    docker-compose up -d prometheus grafana
    print_color $GREEN "✅ Monitoring services started!"
}

start_logging() {
    print_color $GREEN "📝 Starting logging services..."
    docker-compose up -d elasticsearch kibana logstash
    print_color $GREEN "✅ Logging services started!"
}

start_admin() {
    print_color $GREEN "⚙️ Starting admin interfaces..."
    docker-compose up -d pgadmin redis-commander portainer
    print_color $GREEN "✅ Admin interfaces started!"
}

show_status() {
    print_color $BLUE "📋 Service Status:"
    docker-compose ps
}

show_logs() {
    print_color $BLUE "📋 Showing logs for all services..."
    docker-compose logs -f
}

show_app_logs() {
    print_color $BLUE "📋 Showing application logs..."
    docker-compose logs -f app
}

test_services() {
    print_color $BLUE "🧪 Testing all services..."
    
    # Test PostgreSQL
    print_color $YELLOW "Testing PostgreSQL..."
    docker exec ktor-todo-postgres pg_isready -U postgres && print_color $GREEN "✅ PostgreSQL OK" || print_color $RED "❌ PostgreSQL FAILED"
    
    # Test Redis
    print_color $YELLOW "Testing Redis..."
    docker exec ktor-todo-redis redis-cli ping && print_color $GREEN "✅ Redis OK" || print_color $RED "❌ Redis FAILED"
    
    # Test Application (if running)
    print_color $YELLOW "Testing Application..."
    curl -s http://localhost:8080/health > /dev/null && print_color $GREEN "✅ Application OK" || print_color $RED "❌ Application not responding"
    
    # Test Nginx
    print_color $YELLOW "Testing Nginx..."
    curl -s http://localhost:80/nginx-health > /dev/null && print_color $GREEN "✅ Nginx OK" || print_color $RED "❌ Nginx not responding"
    
    # Test other services
    print_color $YELLOW "Testing other services..."
    curl -s http://localhost:5050 > /dev/null && print_color $GREEN "✅ pgAdmin OK" || print_color $RED "❌ pgAdmin not responding"
    curl -s http://localhost:3000 > /dev/null && print_color $GREEN "✅ Grafana OK" || print_color $RED "❌ Grafana not responding"
    curl -s http://localhost:9090 > /dev/null && print_color $GREEN "✅ Prometheus OK" || print_color $RED "❌ Prometheus not responding"
    curl -s http://localhost:5601 > /dev/null && print_color $GREEN "✅ Kibana OK" || print_color $RED "❌ Kibana not responding"
    curl -s http://localhost:9001 > /dev/null && print_color $GREEN "✅ MinIO OK" || print_color $RED "❌ MinIO not responding"
}

clean_all() {
    print_color $RED "🧹 Cleaning up all containers and volumes..."
    read -p "This will remove all containers and volumes. Are you sure? (y/N): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        docker-compose down -v --remove-orphans
        docker system prune -a -f
        print_color $GREEN "✅ Cleanup completed!"
    else
        print_color $YELLOW "❌ Cleanup cancelled."
    fi
}

build_app() {
    print_color $GREEN "🔨 Building application image..."
    docker-compose build app
    print_color $GREEN "✅ Application image built!"
}

show_urls() {
    print_color $BLUE "🌐 Service URLs:"
    echo "Application:       http://localhost:8080"
    echo "Nginx Proxy:       http://localhost:80"
    echo "pgAdmin:          http://localhost:5050"
    echo "Redis Commander:  http://localhost:8081"
    echo "Prometheus:       http://localhost:9090"
    echo "Grafana:          http://localhost:3000"
    echo "Kibana:           http://localhost:5601"
    echo "MinIO Console:    http://localhost:9001"
    echo "Portainer:        https://localhost:9443"
}

# Main script logic
case "$1" in
    start-all)
        start_all
        ;;
    stop-all)
        stop_all
        ;;
    restart-all)
        restart_all
        ;;
    start-core)
        start_core
        ;;
    start-monitoring)
        start_monitoring
        ;;
    start-logging)
        start_logging
        ;;
    start-admin)
        start_admin
        ;;
    status)
        show_status
        ;;
    logs)
        show_logs
        ;;
    logs-app)
        show_app_logs
        ;;
    clean)
        clean_all
        ;;
    build)
        build_app
        ;;
    test)
        test_services
        ;;
    help|--help|-h)
        show_help
        ;;
    *)
        if [ -z "$1" ]; then
            show_help
        else
            print_color $RED "❌ Unknown command: $1"
            echo ""
            show_help
        fi
        exit 1
        ;;
esac 