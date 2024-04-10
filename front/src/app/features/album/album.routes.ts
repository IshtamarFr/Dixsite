import { Routes } from '@angular/router';
import { AlbumListComponent } from './components/album-list/album-list.component';
import { FormPicvidComponent } from './components/form-picvid/form-picvid.component';
import { FormMultiplePicvidsComponent } from './components/form-multiple-picvids/form-multiple-picvids.component';
import { PicvidComponent } from './components/picvid/picvid.component';

export const ALBUM_routes: Routes = [
  { path: '', component: AlbumListComponent },
  { path: 'picvid/:picvidId', component: PicvidComponent },
  { path: 'picvid', component: FormPicvidComponent },
  { path: 'picvids', component: FormMultiplePicvidsComponent },
];
