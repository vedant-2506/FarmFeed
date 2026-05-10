window.API_BASE_URL = (() => {
  const host = window.location.hostname;

  if (host === "localhost" || host === "127.0.0.1") {
    return "http://localhost:8080";
  }

  return "https://farmfeed.onrender.com";
})();