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
    albumId: number,
    action: string
  ): Observable<string> {
    return this.httpClient.put(
      `${this.pathService}/${id}/album/${albumId}/status`,
      { action },
      { responseType: 'text' }
    );
  }

  public unSubscribeToAlbum(id: number, albumId: number): Observable<string> {
    return this.httpClient.delete(
      `${this.pathService}/${id}/album/${albumId}/subscribe`,
      { responseType: 'text' }
    );
  }

  public subscribeToAlbum(id: number, albumId: number): Observable<string> {
    return this.httpClient.post(
      `${this.pathService}/${id}/album/${albumId}/subscribe`,
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

  public addModo(
    id: number,
    albumId: number,
    moderatorEmail: string
  ): Observable<Album> {
    return this.httpClient.post<Album>(
      `${this.pathService}/${id}/album/${albumId}/moderation`,
      null,
      { params: { moderatorEmail } }
    );
  }

  public removeModo(
    id: number,
    albumId: number,
    moderatorId: number
  ): Observable<Album> {
    return this.httpClient.delete<Album>(
      `${this.pathService}/${id}/album/${albumId}/moderation/${moderatorId}`
    );
  }
}
