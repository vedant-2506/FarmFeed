/**
 * Vendor Order Management
 * Handles order acceptance, rejection, and shifting order tracking
 */

const API_BASE_URL = window.API_BASE_URL || window.location.origin;

let vendorId = null;
let currentTab = 'orders'; // orders, shifting

document.addEventListener('DOMContentLoaded', () => {
    vendorId = localStorage.getItem('shop_id');
    
    if (!vendorId) {
        window.location.href = 'Login.html';
        return;
    }
    
    // Initialize tabs
    initializeTabs();
    loadPendingOrders();
});

function initializeTabs() {
    const tabs = document.querySelectorAll('.order-tab');
    tabs.forEach(tab => {
        tab.addEventListener('click', (e) => {
            e.preventDefault();
            const tabName = tab.getAttribute('data-tab');
            setActiveTab(tabName);
        });
    });
}

function setActiveTab(tabName) {
    currentTab = tabName;
    
    // Update tab styling
    document.querySelectorAll('.order-tab').forEach(tab => {
        tab.classList.remove('active');
    });
    document.querySelector(`[data-tab="${tabName}"]`).classList.add('active');
    
    // Load content based on tab
    if (tabName === 'orders') {
        loadPendingOrders();
    } else if (tabName === 'shifting') {
        loadShiftingOrders();
    }
}

async function loadPendingOrders() {
    try {
        const response = await fetch(`${API_BASE_URL}/api/orders/vendor/${vendorId}/pending`);
        const data = await response.json();
        
        if (data.success) {
            displayPendingOrders(data.data);
        }
    } catch (error) {
        console.error('Error loading pending orders:', error);
        alert('Failed to load orders');
    }
}

async function loadShiftingOrders() {
    try {
        const response = await fetch(`${API_BASE_URL}/api/orders/vendor/${vendorId}/shifting`);
        const data = await response.json();
        
        if (data.success) {
            displayShiftingOrders(data.data);
        }
    } catch (error) {
        console.error('Error loading shifting orders:', error);
        alert('Failed to load shifting orders');
    }
}

function displayPendingOrders(orders) {
    const container = document.getElementById('pendingOrdersContainer') || createOrdersContainer();
    
    if (!orders || orders.length === 0) {
        container.innerHTML = '<p class="text-muted text-center">No pending orders available</p>';
        return;
    }
    
    let html = `
        <div class="table-responsive">
            <table class="table table-hover">
                <thead class="table-light">
                    <tr>
                        <th>Order ID</th>
                        <th>Product Name</th>
                        <th>Farmer Name</th>
                        <th>Farmer Phone</th>
                        <th>Address</th>
                        <th>Quantity</th>
                        <th>Total Price</th>
                        <th>Date</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
    `;
    
    orders.forEach(order => {
        const orderDate = new Date(order.orderDate).toLocaleDateString();
        html += `
            <tr>
                <td><strong>#${order.id}</strong></td>
                <td>${order.productName || 'N/A'}</td>
                <td>${order.farmerName || 'Farmer'}</td>
                <td>${order.farmerPhone || 'N/A'}</td>
                <td>${order.farmerAddress || order.deliveryAddress || 'N/A'}</td>
                <td>${order.quantity} units</td>
                <td>₹${order.totalPrice.toFixed(2)}</td>
                <td>${orderDate}</td>
                <td>
                    <button class="btn btn-sm btn-success" onclick="acceptOrder(${order.id})">
                        <i class="bi bi-check-lg"></i> Accept
                    </button>
                    <button class="btn btn-sm btn-danger" onclick="rejectOrder(${order.id})">
                        <i class="bi bi-x-lg"></i> Reject
                    </button>
                </td>
            </tr>
        `;
    });
    
    html += `
                </tbody>
            </table>
        </div>
    `;
    
    container.innerHTML = html;
}

function displayShiftingOrders(orders) {
    const container = document.getElementById('shiftingOrdersContainer') || createShiftingContainer();
    
    if (!orders || orders.length === 0) {
        container.innerHTML = '<p class="text-muted text-center">No orders out for delivery</p>';
        return;
    }
    
    let html = `
        <div class="table-responsive">
            <table class="table table-hover">
                <thead class="table-light">
                    <tr>
                        <th>Order ID</th>
                        <th>Product Name</th>
                        <th>Farmer Name</th>
                        <th>Farmer Phone</th>
                        <th>Address</th>
                        <th>Total Price</th>
                        <th>Status</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
    `;
    
    orders.forEach(order => {
        html += `
            <tr>
                <td><strong>#${order.id}</strong></td>
                <td>${order.productName || 'N/A'}</td>
                <td>${order.farmerName || 'Farmer'}</td>
                <td>${order.farmerPhone || 'N/A'}</td>
                <td>${order.farmerAddress || order.deliveryAddress || 'N/A'}</td>
                <td>₹${order.totalPrice.toFixed(2)}</td>
                <td><span class="badge bg-warning">Out for Delivery</span></td>
                <td>
                    <button class="btn btn-sm btn-primary" onclick="markAsDelivered(${order.id})">
                        <i class="bi bi-check-circle"></i> Delivered
                    </button>
                </td>
            </tr>
        `;
    });
    
    html += `
                </tbody>
            </table>
        </div>
    `;
    
    container.innerHTML = html;
}

async function acceptOrder(orderId) {
    if (!confirm('Accept this order?')) return;
    
    try {
        const response = await fetch(`${API_BASE_URL}/api/orders/${orderId}/accept-vendor`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ vendorId: parseInt(vendorId) })
        });
        
        const data = await response.json();
        
        if (data.success) {
            alert('Order accepted successfully!');
            loadPendingOrders();
        } else {
            alert('Error: ' + (data.error || 'Could not accept order'));
        }
    } catch (error) {
        console.error('Error accepting order:', error);
        alert('Server error occurred');
    }
}

async function rejectOrder(orderId) {
    if (!confirm('Reject this order?')) return;
    
    try {
        const response = await fetch(`${API_BASE_URL}/api/orders/${orderId}/reject`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' }
        });
        
        const data = await response.json();
        
        if (data.success) {
            alert('Order rejected');
            loadPendingOrders();
        } else {
            alert('Error: ' + (data.error || 'Could not reject order'));
        }
    } catch (error) {
        console.error('Error rejecting order:', error);
        alert('Server error occurred');
    }
}

async function markAsDelivered(orderId) {
    if (!confirm('Mark this order as delivered?')) return;
    
    try {
        const response = await fetch(`${API_BASE_URL}/api/orders/${orderId}/deliver`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' }
        });
        
        const data = await response.json();
        
        if (data.success) {
            alert('Order marked as delivered!');
            loadShiftingOrders();
        } else {
            alert('Error: ' + (data.error || 'Could not update order'));
        }
    } catch (error) {
        console.error('Error marking as delivered:', error);
        alert('Server error occurred');
    }
}

function createOrdersContainer() {
    const container = document.createElement('div');
    container.id = 'pendingOrdersContainer';
    container.className = 'card p-4';
    document.body.appendChild(container);
    return container;
}

function createShiftingContainer() {
    const container = document.createElement('div');
    container.id = 'shiftingOrdersContainer';
    container.className = 'card p-4';
    document.body.appendChild(container);
    return container;
}
