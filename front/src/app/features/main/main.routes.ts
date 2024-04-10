import { Routes } from '@angular/router';
import { MainComponent } from './components/main/main.component';
import { MeComponent } from '../auth/components/me/me.component';
import { FormComponent } from './components/form/form.component';
import { AlbumsComponent } from './components/albums/albums.component';
import { CommentsComponent } from './components/comments/comments.component';

export const MAIN_routes: Routes = [
  { path: '', component: MainComponent },
  { path: 'me', component: MeComponent },
  { path: 'create', component: FormComponent },
  { path: 'albums', component: AlbumsComponent },
  { path: 'comments', component: CommentsComponent },
];
