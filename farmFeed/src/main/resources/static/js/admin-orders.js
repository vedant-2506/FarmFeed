/**
 * Admin Order Management
 * Dashboard for viewing all orders, their status, and statistics
 */

const API_BASE_URL = window.API_BASE_URL || window.location.origin;

let adminId = null;
let currentFilter = 'all'; // all, pending, shifting, delivered

document.addEventListener('DOMContentLoaded', () => {
    adminId = localStorage.getItem('admin_id');
    
    if (!adminId) {
        window.location.href = 'AdminLogin.html';
        return;
    }
    
    loadOrderStatistics();
    loadAllOrders();
    initializeFilterButtons();
});

function initializeFilterButtons() {
    const filterButtons = document.querySelectorAll('.order-filter-btn');
    filterButtons.forEach(btn => {
        btn.addEventListener('click', (e) => {
            e.preventDefault();
            const filter = btn.getAttribute('data-filter');
            setFilter(filter);
        });
    });
}

function setFilter(filter) {
    currentFilter = filter;
    
    // Update button styling
    document.querySelectorAll('.order-filter-btn').forEach(btn => {
        btn.classList.remove('active');
    });
    document.querySelector(`[data-filter="${filter}"]`).classList.add('active');
    
    // Reload orders
    if (filter === 'all') {
        loadAllOrders();
    } else {
        loadOrdersByStatus(filter);
    }
}

async function loadOrderStatistics() {
    try {
        const response = await fetch(`${API_BASE_URL}/api/orders/admin/stats`);
        const data = await response.json();
        
        if (data.success) {
            displayStatistics(data.data);
        }
    } catch (error) {
        console.error('Error loading statistics:', error);
    }
}

function displayStatistics(stats) {
    const statsContainer = document.getElementById('orderStatsContainer');
    
    if (!statsContainer) return;
    
    const statsHTML = `
        <div class="row">
            <div class="col-md-3">
                <div class="stat-card">
                    <div class="stat-icon">
                        <i class="bi bi-bag"></i>
                    </div>
                    <div class="stat-info">
                        <h6 class="text-muted small mb-1">Total Orders</h6>
                        <h4 class="mb-0">${stats.totalOrders || 0}</h4>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="stat-card">
                    <div class="stat-icon">
                        <i class="bi bi-clock-history text-warning"></i>
                    </div>
                    <div class="stat-info">
                        <h6 class="text-muted small mb-1">Pending</h6>
                        <h4 class="text-warning mb-0">${stats.pendingOrders || 0}</h4>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="stat-card">
                    <div class="stat-icon">
                        <i class="bi bi-truck text-info"></i>
                    </div>
                    <div class="stat-info">
                        <h6 class="text-muted small mb-1">Shifting</h6>
                        <h4 class="text-info mb-0">${stats.shiftingOrders || 0}</h4>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="stat-card">
                    <div class="stat-icon">
                        <i class="bi bi-check-circle text-success"></i>
                    </div>
                    <div class="stat-info">
                        <h6 class="text-muted small mb-1">Delivered</h6>
                        <h4 class="text-success mb-0">${stats.deliveredOrders || 0}</h4>
                    </div>
                </div>
            </div>
        </div>
        <div class="row mt-3">
            <div class="col-md-6">
                <div class="stat-card">
                    <h6 class="text-muted small mb-2">Total Revenue</h6>
                    <h4 class="text-success mb-0">₹${(stats.totalRevenue || 0).toFixed(2)}</h4>
                </div>
            </div>
        </div>
    `;
    
    statsContainer.innerHTML = statsHTML;
}

async function loadAllOrders() {
    try {
        const response = await fetch(`${API_BASE_URL}/api/orders/admin/all`);
        const data = await response.json();
        
        if (data.success) {
            displayAllOrders(data.data);
        }
    } catch (error) {
        console.error('Error loading orders:', error);
        alert('Failed to load orders');
    }
}

async function loadOrdersByStatus(status) {
    try {
        const response = await fetch(`${API_BASE_URL}/api/orders/admin/all?status=${status}`);
        const data = await response.json();
        
        if (data.success) {
            displayAllOrders(data.data);
        }
    } catch (error) {
        console.error('Error loading orders:', error);
        alert('Failed to load orders');
    }
}

function displayAllOrders(orders) {
    const container = document.getElementById('ordersTableContainer');
    
    if (!orders || orders.length === 0) {
        container.innerHTML = '<p class="text-muted text-center p-4">No orders found</p>';
        return;
    }
    
    let html = `
        <div class="table-responsive">
            <table class="table table-hover table-striped">
                <thead class="table-light">
                    <tr>
                        <th>Order ID</th>
                        <th>Farmer</th>
                        <th>Vendor</th>
                        <th>Product</th>
                        <th>Quantity</th>
                        <th>Total Amount</th>
                        <th>Status</th>
                        <th>Order Date</th>
                        <th>Payment</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
    `;
    
    orders.forEach(order => {
        const orderDate = new Date(order.orderDate).toLocaleDateString();
        const statusBadge = getStatusBadgeHTML(order.status);
        
        html += `
            <tr>
                <td><strong>#${order.id}</strong></td>
                <td>${order.farmerName || 'Farmer ' + order.farmerId}</td>
                <td>${order.vendorId ? 'Vendor ' + order.vendorId : '<span class="text-muted">Unassigned</span>'}</td>
                <td>${order.productName || order.productId}</td>
                <td>${order.quantity}</td>
                <td>₹${order.totalPrice.toFixed(2)}</td>
                <td>${statusBadge}</td>
                <td>${orderDate}</td>
                <td>
                    <span class="badge ${order.isPaid ? 'bg-success' : 'bg-warning'}">
                        ${order.isPaid ? 'Paid' : 'Unpaid'}
                    </span>
                </td>
                <td>
                    <button class="btn btn-sm btn-primary" onclick="viewOrderDetails(${order.id})">
                        <i class="bi bi-eye"></i> View
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

function getStatusBadgeHTML(status) {
    const badgeClasses = {
        'pending': 'badge bg-warning text-dark',
        'shifting': 'badge bg-info',
        'delivered': 'badge bg-success',
        'cancelled': 'badge bg-danger',
        'accepted': 'badge bg-primary'
    };
    
    const badgeClass = badgeClasses[status.toLowerCase()] || 'badge bg-secondary';
    return `<span class="${badgeClass}">${status.toUpperCase()}</span>`;
}

function viewOrderDetails(orderId) {
    // Open order details modal or navigate to details page
    fetch(`${API_BASE_URL}/api/orders/${orderId}`)
        .then(res => res.json())
        .then(data => {
            if (data.success) {
                displayOrderDetailsModal(data.data);
            }
        })
        .catch(err => alert('Error loading order details'));
}

function displayOrderDetailsModal(order) {
    const modalHTML = `
        <div class="modal fade" id="orderDetailsModal" tabindex="-1">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">Order #${order.id} Details</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div class="row mb-3">
                            <div class="col-md-6">
                                <h6 class="text-muted">Farmer Information</h6>
                                <p><strong>${order.farmerName}</strong><br>
                                Phone: ${order.farmerPhone}<br>
                                Address: ${order.farmerAddress || order.deliveryAddress}</p>
                            </div>
                            <div class="col-md-6">
                                <h6 class="text-muted">Order Status</h6>
                                <p>${getStatusBadgeHTML(order.status)}<br>
                                <small class="text-muted">Payment: ${order.paymentMethod || 'Cash'}</small></p>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6">
                                <h6 class="text-muted">Product Details</h6>
                                <p><strong>${order.productName || order.productId}</strong><br>
                                Quantity: ${order.quantity} units<br>
                                Price: ₹${order.totalPrice.toFixed(2)}</p>
                            </div>
                            <div class="col-md-6">
                                <h6 class="text-muted">Vendor Information</h6>
                                <p>${order.vendorId ? 'Vendor ID: ' + order.vendorId : '<span class="text-muted">Not yet assigned</span>'}</p>
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                    </div>
                </div>
            </div>
        </div>
    `;
    
    // Remove old modal if exists
    const oldModal = document.getElementById('orderDetailsModal');
    if (oldModal) oldModal.remove();
    
    // Add new modal
    document.body.insertAdjacentHTML('beforeend', modalHTML);
    
    // Show modal
    const modal = new bootstrap.Modal(document.getElementById('orderDetailsModal'));
    modal.show();
}

// Auto-refresh orders every 60 seconds
setInterval(() => {
    loadOrderStatistics();
    if (currentFilter === 'all') {
        loadAllOrders();
    } else {
        loadOrdersByStatus(currentFilter);
    }
}, 60000);
