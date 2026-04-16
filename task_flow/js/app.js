/**
 * Smart Task Management System
 * Shared JS utilities, API helpers, token management
 */

const BASE_URL = "http://localhost:8080/api";

// ─── Token Management ────────────────────────────────────────────────────────
const Auth = {
  getToken: () => localStorage.getItem("token"),
  setToken: (token) => localStorage.setItem("token", token),
  removeToken: () => localStorage.removeItem("token"),
  getUser: () => {
    const u = localStorage.getItem("user");
    return u ? JSON.parse(u) : null;
  },
  setUser: (user) => localStorage.setItem("user", JSON.stringify(user)),
  removeUser: () => localStorage.removeItem("user"),
  logout: () => {
    Auth.removeToken();
    Auth.removeUser();
    window.location.href = "index.html";
  },
  requireAuth: () => {
    if (!Auth.getToken()) {
      window.location.href = "index.html";
      return false;
    }
    return true;
  },
};

// ─── API Utility ─────────────────────────────────────────────────────────────
const API = {
  headers: () => ({
    "Content-Type": "application/json",
    Authorization: "Bearer " + Auth.getToken(),
  }),

  get: (path, params = {}) =>
    $.ajax({
      url: BASE_URL + path,
      method: "GET",
      headers: API.headers(),
      data: params,
      beforeSend: () => Loader.show(),
      complete: () => Loader.hide(),
      error: API.handleError,
    }),

  post: (path, data) =>
    $.ajax({
      url: BASE_URL + path,
      method: "POST",
      headers: API.headers(),
      contentType: "application/json",
      data: JSON.stringify(data),
      beforeSend: () => Loader.show(),
      complete: () => Loader.hide(),
      error: API.handleError,
    }),

  put: (path, data) =>
    $.ajax({
      url: BASE_URL + path,
      method: "PUT",
      headers: API.headers(),
      contentType: "application/json",
      data: JSON.stringify(data),
      beforeSend: () => Loader.show(),
      complete: () => Loader.hide(),
      error: API.handleError,
    }),

  delete: (path) =>
    $.ajax({
      url: BASE_URL + path,
      method: "DELETE",
      headers: API.headers(),
      beforeSend: () => Loader.show(),
      complete: () => Loader.hide(),
      error: API.handleError,
    }),

  handleError: (xhr) => {
    Loader.hide();
    if (xhr.status === 401) {
      if (xhr.responseJSON.error === "TOKEN_EXPIRED") {
        Toast.show("Session expired. Please login again.", "error");
      } else if (xhr.responseJSON.error === "MALFORMED_TOKEN") {
        Toast.show("Token Malformed. Redirecting for login.", "error");
      } else if (xhr.responseJSON.error === "INVALID_TOKEN") {
        Toast.show("Invalid JWT Token. Redirecting for login.", "error");
      } else if (xhr.responseJSON.error === "AUTH_ERROR") {
        Toast.show("Something went wrong.", "error");
      }
      setTimeout(() => Auth.logout(), 1500);
    } else if (xhr.status === 403) {
      Toast.show("You don't have permission for this action.", "error");
    } else if (xhr.status === 404) {
      Toast.show("Resource not found.", "error");
    } else {
      const msg = xhr.responseJSON?.message || "Something went wrong.";
      Toast.show(msg, "error");
    }
  },
};

// ─── Toast Notifications ─────────────────────────────────────────────────────
const Toast = {
  container: null,
  init: () => {
    if (!$("#toast-container").length) {
      $("body").append('<div id="toast-container" class="fixed top-5 right-5 z-[9999] flex flex-col gap-2"></div>');
    }
    Toast.container = $("#toast-container");
  },
  show: (message, type = "info", duration = 3500) => {
    Toast.init();
    const icons = {
      success: `<svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"/></svg>`,
      error: `<svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/></svg>`,
      warning: `<svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01M10.29 3.86L1.82 18a2 2 0 001.71 3h16.94a2 2 0 001.71-3L13.71 3.86a2 2 0 00-3.42 0z"/></svg>`,
      info: `<svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/></svg>`,
    };
    const colors = {
      success: "bg-emerald-500",
      error: "bg-red-500",
      warning: "bg-amber-500",
      info: "bg-blue-500",
    };
    const id = "toast-" + Date.now();
    const toast = $(`
      <div id="${id}" class="flex items-center gap-3 px-4 py-3 rounded-xl text-white text-sm font-medium shadow-xl ${colors[type]} min-w-[280px] max-w-xs transform translate-x-full transition-all duration-300">
        ${icons[type]}
        <span class="flex-1">${message}</span>
        <button onclick="$('#${id}').remove()" class="opacity-70 hover:opacity-100 ml-1">✕</button>
      </div>
    `);
    Toast.container.append(toast);
    setTimeout(() => toast.removeClass("translate-x-full"), 10);
    setTimeout(() => {
      toast.addClass("translate-x-full opacity-0");
      setTimeout(() => toast.remove(), 300);
    }, duration);
  },
};

// ─── Loader ──────────────────────────────────────────────────────────────────
const Loader = {
  count: 0,
  init: () => {
    if (!$("#global-loader").length) {
      $("body").append(`
        <div id="global-loader" class="fixed inset-0 bg-slate-900/30 backdrop-blur-sm z-[9998] hidden items-center justify-center">
          <div class="bg-white rounded-2xl p-6 shadow-2xl flex items-center gap-4">
            <div class="w-8 h-8 border-4 border-blue-200 border-t-blue-600 rounded-full animate-spin"></div>
            <span class="text-slate-700 font-medium">Loading...</span>
          </div>
        </div>
      `);
    }
  },
  show: () => {
    Loader.init();
    Loader.count++;
    $("#global-loader").removeClass("hidden").addClass("flex");
  },
  hide: () => {
    Loader.count = Math.max(0, Loader.count - 1);
    if (Loader.count === 0) {
      $("#global-loader").addClass("hidden").removeClass("flex");
    }
  },
};

// ─── Confirm Dialog ───────────────────────────────────────────────────────────
const Confirm = {
  show: (message, onConfirm, title = "Are you sure?") => {
    $("#confirm-modal").remove();
    $("body").append(`
      <div id="confirm-modal" class="fixed inset-0 bg-slate-900/50 backdrop-blur-sm z-[9997] flex items-center justify-center p-4">
        <div class="bg-white rounded-2xl shadow-2xl p-6 max-w-sm w-full">
          <div class="flex items-center gap-3 mb-3">
            <div class="w-10 h-10 rounded-full bg-red-100 flex items-center justify-center flex-shrink-0">
              <svg class="w-5 h-5 text-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01M10.29 3.86L1.82 18a2 2 0 001.71 3h16.94a2 2 0 001.71-3L13.71 3.86a2 2 0 00-3.42 0z"/></svg>
            </div>
            <h3 class="text-lg font-bold text-slate-800">${title}</h3>
          </div>
          <p class="text-slate-500 text-sm mb-6 pl-13">${message}</p>
          <div class="flex gap-3 justify-end">
            <button id="confirm-cancel" class="px-4 py-2 rounded-xl border border-slate-200 text-slate-600 hover:bg-slate-50 text-sm font-medium transition-colors">Cancel</button>
            <button id="confirm-ok" class="px-4 py-2 rounded-xl bg-red-600 text-white hover:bg-red-700 text-sm font-medium transition-colors">Logout</button>
          </div>
        </div>
      </div>
    `);
    $("#confirm-cancel").on("click", () => $("#confirm-modal").remove());
    $("#confirm-ok").on("click", () => { $("#confirm-modal").remove(); onConfirm(); });
  },
};

// ─── Sidebar Toggle ───────────────────────────────────────────────────────────
function initSidebar() {
  $("#sidebar-toggle").on("click", () => {
    $("#sidebar").toggleClass("-translate-x-full");
    $("#sidebar-overlay").toggleClass("hidden");
  });
  $("#sidebar-overlay").on("click", () => {
    $("#sidebar").addClass("-translate-x-full");
    $("#sidebar-overlay").addClass("hidden");
  });
}

// ─── Set User Info in Nav ─────────────────────────────────────────────────────
function initUserNav() {
  const user = Auth.getUser();
  if (user) {
    const initials = (user.name || user.email || "U").substring(0, 2).toUpperCase();
    $(".user-avatar").text(initials);
    $(".user-name").text(user.name || user.email || "User");
  }
  $(".logout-btn").on("click", (e) => {
    e.preventDefault();
    Confirm.show("You will be logged out of the system.", () => Auth.logout(), "Logout?");
  });
}

// ─── Priority Badge Helper ────────────────────────────────────────────────────
function priorityBadge(priority) {
  const map = {
    HIGH: "bg-red-100 text-red-700 border border-red-200",
    MEDIUM: "bg-amber-100 text-amber-700 border border-amber-200",
    LOW: "bg-emerald-100 text-emerald-700 border border-emerald-200",
  };
  return `<span class="inline-flex items-center gap-1 px-2 py-0.5 rounded-full text-xs font-semibold ${map[priority] || "bg-slate-100 text-slate-600"}">${priority || "—"}</span>`;
}

// ─── Status Badge Helper ──────────────────────────────────────────────────────
function statusBadge(status) {
  const map = {
    TODO: "bg-slate-100 text-slate-600",
    IN_PROGRESS: "bg-blue-100 text-blue-700",
    IN_REVIEW: "bg-purple-100 text-purple-700",
    DONE: "bg-emerald-100 text-emerald-700",
  };
  const label = (status || "TODO").replace("_", " ");
  return `<span class="px-2 py-0.5 rounded-full text-xs font-semibold ${map[status] || "bg-slate-100 text-slate-600"}">${label}</span>`;
}

// ─── Format Date ──────────────────────────────────────────────────────────────
function formatDate(dateStr) {
  if (!dateStr) return "—";
  const d = new Date(dateStr);
  return d.toLocaleDateString("en-IN", { day: "2-digit", month: "short", year: "numeric" });
}

function isOverdue(dateStr) {
  if (!dateStr) return false;
  return new Date(dateStr) < new Date();
}

// ─── Empty State ──────────────────────────────────────────────────────────────
function emptyState(msg = "No data found", sub = "") {
  return `
    <div class="flex flex-col items-center justify-center py-20 text-center">
      <div class="w-20 h-20 rounded-full bg-slate-100 flex items-center justify-center mb-4">
        <svg class="w-10 h-10 text-slate-300" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"/></svg>
      </div>
      <p class="text-slate-500 font-semibold text-lg">${msg}</p>
      ${sub ? `<p class="text-slate-400 text-sm mt-1">${sub}</p>` : ""}
    </div>
  `;
}
