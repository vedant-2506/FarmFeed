/**
 * Password Toggle Utility
 * Provides functions to toggle password visibility with eye icon
 */

function initPasswordToggles() {
    // Find all password inputs with toggle attribute
    document.querySelectorAll('input[data-toggle="password"]').forEach(passwordInput => {
        // Create toggle button
        const toggleBtn = document.createElement('button');
        toggleBtn.type = 'button';
        toggleBtn.className = 'password-toggle-btn';
        toggleBtn.innerHTML = '<i class="bi bi-eye"></i>';
        toggleBtn.style.cssText = `
            position: absolute;
            right: 10px;
            top: 50%;
            transform: translateY(-50%);
            background: none;
            border: none;
            cursor: pointer;
            color: #666;
            font-size: 1.2rem;
            padding: 5px;
        `;
        
        // Wrap input in container for positioning
        const container = document.createElement('div');
        container.style.position = 'relative';
        container.style.display = 'inline-block';
        container.style.width = '100%';
        
        passwordInput.parentNode.insertBefore(container, passwordInput);
        container.appendChild(passwordInput);
        container.appendChild(toggleBtn);
        
        // Toggle password visibility
        toggleBtn.addEventListener('click', (e) => {
            e.preventDefault();
            togglePasswordVisibility(passwordInput, toggleBtn);
        });
    });
}

function togglePasswordVisibility(inputElement, toggleButton) {
    if (inputElement.type === 'password') {
        inputElement.type = 'text';
        toggleButton.innerHTML = '<i class="bi bi-eye-slash"></i>';
    } else {
        inputElement.type = 'password';
        toggleButton.innerHTML = '<i class="bi bi-eye"></i>';
    }
}

// Initialize on DOM load
document.addEventListener('DOMContentLoaded', initPasswordToggles);
