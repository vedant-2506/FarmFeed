/**
 * Farmer Purchase History & Order Tracking
 * Displays orders with status updates: Pending -> Shifting -> Delivered
 */

const API_BASE_URL = window.API_BASE_URL || window.location.origin;

let farmerId = null;

document.addEventListener('DOMContentLoaded', () => {
    farmerId = localStorage.getItem('farmer_id');
    
    if (!farmerId) {
        window.location.href = 'Login.html';
        return;
    }
    
    loadPurchaseHistory();
});

async function loadPurchaseHistory() {
    try {
        const response = await fetch(`${API_BASE_URL}/api/orders/farmer/${farmerId}`);
        const data = await response.json();
        
        if (data.success) {
            displayPurchaseHistory(data.data);
        }
    } catch (error) {
        console.error('Error loading orders:', error);
        alert('Failed to load order history');
    }
}

function displayPurchaseHistory(orders) {
    const container = document.getElementById('ordersContainer');
    
    if (!orders || orders.length === 0) {
        container.innerHTML = '<p class="text-muted text-center">No orders yet. <a href="Home.html">Start Shopping!</a></p>';
        return;
    }
    
    let html = '';
    
    orders.forEach(order => {
        const orderDate = new Date(order.orderDate).toLocaleDateString();
        const statusBadge = getStatusBadge(order.status);
        
        html += `
            <div class="order-item card mb-3 border-left-success">
                <div class="card-body">
                    <div class="row">
                        <div class="col-md-6">
                            <h6 class="card-title mb-2">
                                Order #${order.id}
                                ${statusBadge}
                            </h6>
                            <p class="mb-1"><strong>Product:</strong> ${order.productName || `Product #${order.productId}`}</p>
                            <p class="mb-1"><strong>Quantity:</strong> ${order.quantity} units</p>
                            <p class="mb-0"><strong>Date:</strong> ${orderDate}</p>
                        </div>
                        <div class="col-md-6 text-md-end">
                            <h5 class="text-success mb-2">₹${order.totalPrice.toFixed(2)}</h5>
                            <p class="mb-1"><strong>Payment:</strong> ${order.paymentMethod || 'Cash'}</p>
                            <p class="mb-0">
                                <strong>Status:</strong> 
                                <span class="badge ${getStatusColor(order.status)}">${order.status.toUpperCase()}</span>
                            </p>
                        </div>
                    </div>
                    ${getOrderTimeline(order.status)}
                </div>
            </div>
        `;
    });
    
    container.innerHTML = html;
}

function getStatusBadge(status) {
    const badges = {
        'pending': '<span class="badge bg-warning ms-2">Pending</span>',
        'shifting': '<span class="badge bg-info ms-2">Shifting</span>',
        'delivered': '<span class="badge bg-success ms-2">Delivered</span>',
        'cancelled': '<span class="badge bg-danger ms-2">Cancelled</span>',
        'rejected': '<span class="badge bg-danger ms-2">Rejected</span>'
    };
    return badges[status.toLowerCase()] || '';
}

function getStatusColor(status) {
    const colors = {
        'pending': 'bg-warning text-dark',
        'shifting': 'bg-info',
        'delivered': 'bg-success',
        'cancelled': 'bg-danger',
        'rejected': 'bg-danger'
    };
    return colors[status.toLowerCase()] || 'bg-secondary';
}

function getOrderTimeline(status) {
    const currentStatus = status.toLowerCase();
    const steps = ['pending', 'shifting', 'delivered'];
    const currentStep = steps.indexOf(currentStatus);
    
    let timeline = '<div class="order-timeline mt-3"><div class="d-flex gap-2">';
    
    steps.forEach((step, index) => {
        const isCompleted = index <= currentStep;
        const isCurrent = index === currentStep;
        
        timeline += `
            <div class="flex-grow-1 text-center">
                <div class="step-indicator ${isCurrent ? 'active' : ''} ${isCompleted ? 'completed' : ''}">
                    <i class="bi ${getStepIcon(step)}"></i>
                </div>
                <small class="d-block mt-1">${formatStepName(step)}</small>
            </div>
        `;
        
        if (index < steps.length - 1) {
            timeline += `<div class="flex-grow-0" style="width: 2px; background: ${isCompleted && index < currentStep ? '#28a745' : '#ddd'};"></div>`;
        }
    });
    
    timeline += '</div></div>';
    return timeline;
}

function getStepIcon(step) {
    const icons = {
        'pending': 'bi-clock-history',
        'shifting': 'bi-truck',
        'delivered': 'bi-check-circle'
    };
    return icons[step] || 'bi-question-circle';
}

function formatStepName(step) {
    const names = {
        'pending': 'Order Placed',
        'shifting': 'Out for Delivery',
        'delivered': 'Delivered'
    };
    return names[step] || step;
}

// Auto-refresh order status every 30 seconds
setInterval(loadPurchaseHistory, 30000);
