<<<<<<< HEAD
// ============================================================
// FarmFeed - Global Config
// ============================================================
// Detect environment and set appropriate backend URL
if (window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1') {
  // Local development
  window.API_BASE_URL = 'http://localhost:8080';
} else if (window.location.hostname === 'farmfeed-three.vercel.app') {
  // Production - Vercel frontend to Render backend
  window.API_BASE_URL = 'https://farmfeed.onrender.com';
} else {
  // Fallback to current origin
  window.API_BASE_URL = window.location.origin;
}
=======
window.API_BASE_URL = "https://farmfeed.onrender.com";
>>>>>>> 80581568b497d44057ac1e76cfd3dc0e15879263
