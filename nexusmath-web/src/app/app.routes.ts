import { Routes } from '@angular/router';
import { authGuard } from './security/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: 'login', loadComponent: () => import('./pages/login/login.component').then((m) => m.LoginComponent) },
  { path: 'dashboard', loadComponent: () => import('./pages/dashboard/dashboard.component').then((m) => m.DashboardComponent) },
  { path: 'about', canActivate: [authGuard], loadComponent: () => import('./pages/about/about.component').then((m) => m.AboutComponent) },
  { path: 'theory', canActivate: [authGuard], loadComponent: () => import('./pages/theory/theory.component').then((m) => m.TheoryComponent) },
  { path: 'math', canActivate: [authGuard], loadComponent: () => import('./pages/math/math.component').then((m) => m.MathComponent) },
  { path: 'agents', canActivate: [authGuard], loadComponent: () => import('./pages/agents/agents.component').then((m) => m.AgentsComponent) },
  { path: 'guide', canActivate: [authGuard], loadComponent: () => import('./pages/guide/guide.component').then((m) => m.GuideComponent) },
  { path: '**', redirectTo: 'dashboard' },
];
