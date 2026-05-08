/**
 * Toast Notification Utility for FarmFeed
 */
const Toast = {
    init() {
        if (!document.getElementById('toast-container')) {
            const container = document.createElement('div');
            container.id = 'toast-container';
            container.className = 'toast-container';
            document.body.appendChild(container);
        }
    },

    show(message, type = 'success', title = '') {
        this.init();
        const container = document.getElementById('toast-container');
        
        const toast = document.createElement('div');
        toast.className = `toast-item toast-${type}`;
        
        const icons = {
            success: 'bi-check-circle-fill',
            error: 'bi-x-circle-fill',
            warning: 'bi-exclamation-triangle-fill',
            info: 'bi-info-circle-fill'
        };
        
        const defaultTitles = {
            success: 'Success',
            error: 'Error',
            warning: 'Warning',
            info: 'Information'
        };

        const displayTitle = title || defaultTitles[type];
        
        toast.innerHTML = `
            <div class="toast-icon">
                <i class="bi ${icons[type]}"></i>
            </div>
            <div class="toast-content">
                <div class="toast-title">${displayTitle}</div>
                <div class="toast-message">${message}</div>
            </div>
            <button class="toast-close">
                <i class="bi bi-x"></i>
            </button>
        `;
        
        container.appendChild(toast);
        
        // Auto remove
        const timeout = setTimeout(() => {
            this.remove(toast);
        }, 5000);
        
        // Close button
        toast.querySelector('.toast-close').addEventListener('click', () => {
            clearTimeout(timeout);
            this.remove(toast);
        });
    },

    remove(toast) {
        toast.classList.add('fade-out');
        setTimeout(() => {
            if (toast.parentNode) {
                toast.parentNode.removeChild(toast);
            }
        }, 300);
    },

    success(msg, title) { this.show(msg, 'success', title); },
    error(msg, title) { this.show(msg, 'error', title); },
    warning(msg, title) { this.show(msg, 'warning', title); },
    info(msg, title) { this.show(msg, 'info', title); }
};

// Global alert override (Optional but requested)
// window.alert = (msg) => Toast.info(msg);
