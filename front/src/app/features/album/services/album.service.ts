import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AlbumRequestResponse } from '../../main/interfaces/albums-request-response.interface';
import { Album } from '../../main/interfaces/album.interface';

@Injectable({
  providedIn: 'root',
})
export class AlbumService {
  private pathService = 'api/user';

  constructor(private httpClient: HttpClient) {}

  public create(id: number, request: FormData): Observable<string> {
    return this.httpClient.post(`${this.pathService}/${id}/album`, request, {
      responseType: 'text',
    });
  }

  public modify(id: number, request: FormData): Observable<Album> {
    return this.httpClient.put<Album>(`api/album/${id}`, request);
  }

  public delete(id: number): Observable<string> {
    return this.httpClient.delete(`api/album/${id}`, { responseType: 'text' });
  }

  public list(id: number): Observable<AlbumRequestResponse> {
    return this.httpClient.get<AlbumRequestResponse>(
      `${this.pathService}/${id}/album`
    );
  }

  public listAll(): Observable<Album[]> {
    return this.httpClient.get<Album[]>('api/album');
  }

  public changeStatus(
    id: number,
    idAlbum: number,
    action: string
  ): Observable<string> {
    return this.httpClient.put(
      `${this.pathService}/${id}/album/${idAlbum}/status`,
      { action },
      { responseType: 'text' }
    );
  }

  public unSubscribeToAlbum(id: number, idAlbum: number): Observable<string> {
    return this.httpClient.delete(
      `${this.pathService}/${id}/album/${idAlbum}/subscribe`,
      { responseType: 'text' }
    );
  }

  public subscribeToAlbum(id: number, idAlbum: number): Observable<string> {
    return this.httpClient.post(
      `${this.pathService}/${id}/album/${idAlbum}/subscribe`,
      null,
      { responseType: 'text' }
    );
  }

  public getImageURL(imageId: string): string {
    return `api/file/crop-${imageId}`;
  }

  public getAlbumById(id: number): Observable<Album> {
    return this.httpClient.get<Album>(`api/album/${id}`);
  }
}
