import { Routes } from '@angular/router';
import { UnauthGuard } from './guards/unauth.guard';
import { AuthGuard } from './guards/auth.guard';
import { NotFoundComponent } from './components/pages-html/not-found/not-found.component';
import { IshtaComponent } from './features/dev/ishta/ishta.component';
import { MentionsComponent } from './components/pages-html/mentions/mentions.component';
import { MaintenanceComponent } from './components/pages-html/maintenance/maintenance.component';

export const routes: Routes = [
  {
    path: '',
    canActivate: [UnauthGuard],
    loadChildren: () =>
      import('./features/auth/auth.routes').then((r) => r.AUTH_routes),
  },
  {
    path: 'main',
    canActivate: [AuthGuard],
    loadChildren: () =>
      import('./features/main/main.routes').then((r) => r.MAIN_routes),
  },
  {
    path: 'album/:albumId',
    canActivate: [AuthGuard],
    loadChildren: () =>
      import('./features/album/album.routes').then((r) => r.ALBUM_routes),
  },
  { path: '404', component: NotFoundComponent },
  { path: 'ishta', component: IshtaComponent },
  { path: 'mentions', component: MentionsComponent },
  { path: 'maintenance', component: MaintenanceComponent },

  { path: '**', redirectTo: '404' },
];
