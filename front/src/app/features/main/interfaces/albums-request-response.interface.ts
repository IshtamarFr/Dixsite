import { Album } from './album.interface';

export interface AlbumRequestResponse {
  owned: Album[];
  subscribed: Album[];
}
