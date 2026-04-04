/**
 * Shared layout: sidebar + topbar HTML generator
 * Include this after app.js on authenticated pages
 */

function renderLayout(activePage) {
  const navItems = [
    { id: "dashboard", label: "Dashboard", href: "dashboard.html", icon: `<svg class="w-5 h-5 sidebar-icon text-slate-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6"/></svg>` },
    { id: "projects", label: "Projects", href: "projects.html", icon: `<svg class="w-5 h-5 sidebar-icon text-slate-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10"/></svg>` },
    { id: "tasks", label: "My Tasks", href: "tasks.html", icon: `<svg class="w-5 h-5 sidebar-icon text-slate-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-6 9l2 2 4-4"/></svg>` },
  ];

  const sidebarHtml = `
    <!-- Sidebar overlay (mobile) -->
    <div id="sidebar-overlay" class="fixed inset-0 bg-slate-900/60 z-20 hidden lg:hidden backdrop-blur-sm"></div>

    <!-- Sidebar -->
    <aside id="sidebar" class="fixed top-0 left-0 h-full w-64 bg-white border-r border-slate-100 z-30 transform -translate-x-full lg:translate-x-0 transition-transform duration-300 shadow-xl lg:shadow-none flex flex-col">
      <!-- Logo -->
      <div class="px-5 py-5 border-b border-slate-100">
        <a href="dashboard.html" class="flex items-center gap-2.5">
          <div class="w-9 h-9 bg-blue-600 rounded-xl flex items-center justify-center shadow-md shadow-blue-600/30">
            <svg class="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-6 9l2 2 4-4"/></svg>
          </div>
          <span class="text-lg font-bold text-slate-800 tracking-tight">TaskFlow</span>
        </a>
      </div>

      <!-- Nav -->
      <nav class="flex-1 px-3 py-4 space-y-0.5 overflow-y-auto">
        <p class="text-xs font-semibold text-slate-400 uppercase tracking-widest px-3 mb-2">Main</p>
        ${navItems.map(item => `
          <a href="${item.href}" class="sidebar-link flex items-center gap-3 px-3 py-2.5 rounded-xl text-sm font-medium text-slate-600 hover:bg-slate-50 hover:text-slate-800 transition-all duration-150 group ${activePage === item.id ? "active" : ""}">
            ${item.icon}
            <span>${item.label}</span>
          </a>
        `).join("")}
      </nav>

      <!-- User + Logout -->
      <div class="px-3 py-4 border-t border-slate-100">
        <div class="flex items-center gap-3 px-3 py-2.5 rounded-xl mb-1">
          <div class="user-avatar w-8 h-8 bg-blue-600 rounded-full flex items-center justify-center text-white text-xs font-bold flex-shrink-0">U</div>
          <div class="min-w-0">
            <p class="user-name text-sm font-semibold text-slate-700 truncate">User</p>
            <!-- <p class="text-xs text-slate-400">Member</p> -->
          </div>
        </div>
        <a href="#" class="logout-btn flex items-center gap-3 px-3 py-2.5 rounded-xl text-sm font-medium text-slate-500 hover:bg-red-50 hover:text-red-600 transition-all duration-150 group">
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"/></svg>
          Logout
        </a>
      </div>
    </aside>
  `;

  const topbarHtml = `
    <!-- Topbar -->
    <header class="h-16 bg-white border-b border-slate-100 flex items-center gap-4 px-4 lg:px-6 sticky top-0 z-10 shadow-sm">
      <!-- Mobile menu toggle -->
      <button id="sidebar-toggle" class="lg:hidden p-2 rounded-xl hover:bg-slate-100 text-slate-500 transition-colors">
        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16"/></svg>
      </button>

      <div class="flex-1"></div>

      <!-- Notification bell -->
      <button class="relative p-2 rounded-xl hover:bg-slate-100 text-slate-400 hover:text-slate-600 transition-colors">
        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"/></svg>
        <span class="absolute top-1.5 right-1.5 w-2 h-2 bg-red-500 rounded-full"></span>
      </button>

      <!-- User -->
      <div class="flex items-center gap-2.5 pl-3 border-l border-slate-100">
        <div class="user-avatar w-8 h-8 bg-blue-600 rounded-full flex items-center justify-center text-white text-xs font-bold">U</div>
        <span class="user-name text-sm font-semibold text-slate-700 hidden sm:block">User</span>
      </div>
    </header>
  `;

  return { sidebarHtml, topbarHtml };
}
