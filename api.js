// ============================================
//  BloodLink — Frontend API Connector
//  Include this in every HTML page:
//  <script src="api.js"></script>
// ============================================

const API = {
  BASE: 'http://localhost:8080/bloodlink',

  // ── HELPER ─────────────────────────────────
  async request(method, url, body = null) {
    const options = {
      method,
      headers: { 'Content-Type': 'application/json' },
      credentials: 'include', // send session cookies
    };
    if (body) options.body = JSON.stringify(body);
    try {
      const res  = await fetch(this.BASE + url, options);
      const data = await res.json();
      return data;
    } catch (err) {
      console.error('API Error:', err);
      return { success: false, error: 'Cannot connect to server. Is Tomcat running?' };
    }
  },

  get(url)          { return this.request('GET',    url); },
  post(url, body)   { return this.request('POST',   url, body); },
  put(url, body)    { return this.request('PUT',    url, body); },
  delete(url)       { return this.request('DELETE', url); },

  // ── AUTH ────────────────────────────────────
  auth: {
    register(data)  { return API.post('/api/auth/register', data); },
    login(data)     { return API.post('/api/auth/login',    data); },
    logout()        { return API.post('/api/auth/logout',   {}); },
    me()            { return API.get('/api/auth/me'); },
  },

  // ── DONORS ──────────────────────────────────
  donors: {
    search(bloodGroup = '', city = '') {
      const params = new URLSearchParams();
      if (bloodGroup) params.append('bloodGroup', bloodGroup);
      if (city)       params.append('city', city);
      return API.get('/api/donors/search?' + params.toString());
    },
    getById(id)       { return API.get('/api/donors/' + id); },
    getMe()           { return API.get('/api/donors/me'); },
    setAvailability(available) {
      return API.put('/api/donors/availability', { available });
    },
  },

  // ── BLOOD REQUESTS ──────────────────────────
  requests: {
    getActive()    { return API.get('/api/requests'); },
    getAll()       { return API.get('/api/requests/all'); },
    create(data)   { return API.post('/api/requests', data); },
    updateStatus(id, status) {
      return API.put('/api/requests/' + id, { status });
    },
  },

  // ── ADMIN ───────────────────────────────────
  admin: {
    stats()    { return API.get('/api/admin/stats'); },
    donors()   { return API.get('/api/admin/donors'); },
    requests() { return API.get('/api/admin/requests'); },
  },
};


// ============================================
//  SESSION HELPER — checks login on page load
// ============================================
async function checkLogin(redirectIfLoggedOut = false) {
  const data = await API.auth.me();
  if (data.success) {
    return data.user;
  } else {
    if (redirectIfLoggedOut) {
      window.location.href = 'login.html';
    }
    return null;
  }
}


// ============================================
//  TOAST HELPER (works on all pages)
// ============================================
function showToast(msg, type = 'default') {
  let toast = document.getElementById('toast');
  if (!toast) {
    toast = document.createElement('div');
    toast.id = 'toast';
    toast.style.cssText = `
      position:fixed; bottom:2rem; right:2rem;
      background:#1C0A0E; color:white;
      border-radius:12px; padding:0.9rem 1.4rem;
      font-size:0.9rem; font-weight:500;
      z-index:9999; transform:translateX(150%);
      transition:transform 0.4s ease; max-width:320px;
      font-family:'Plus Jakarta Sans',sans-serif;
    `;
    document.body.appendChild(toast);
  }
  toast.textContent = msg;
  toast.style.transform = 'translateX(0)';
  setTimeout(() => toast.style.transform = 'translateX(150%)', 3500);
}
