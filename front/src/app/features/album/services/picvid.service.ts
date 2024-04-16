import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Picvid } from '../interfaces/picvid.interface';

@Injectable({
  providedIn: 'root',
})
export class PicvidService {
  private pathService = 'api/album';

  constructor(private httpClient: HttpClient) {}

  public createSinglePicvid(
    albumId: number,
    request: FormData
  ): Observable<string> {
    return this.httpClient.post(
      `${this.pathService}/${albumId}/picvid`,
      request,
      { responseType: 'text' }
    );
  }

  public createMultiplePicvids(
    albumId: number,
    request: FormData
  ): Observable<string> {
    return this.httpClient.post(
      `${this.pathService}/${albumId}/picvids`,
      request,
      { responseType: 'text' }
    );
  }

  public getAllPicvidsByAlbumId(albumId: number): Observable<Picvid[]> {
    return this.httpClient.get<Picvid[]>(
      `${this.pathService}/${albumId}/picvid`
    );
  }

  public getPicvidById(albumId: number, picvidId: number): Observable<Picvid> {
    return this.httpClient.get<Picvid>(
      `${this.pathService}/${albumId}/picvid/${picvidId}`
    );
  }

  public getFullPicvidURL(picvidId: string): string {
    return `api/file/${picvidId}`;
  }

  public getImageURL(picvidId: string): string {
    return `api/file/crop-${picvidId}`;
  }

  public deletePicvidById(
    albumId: number,
    picvidId: number
  ): Observable<string> {
    return this.httpClient.delete(
      `${this.pathService}/${albumId}/picvid/${picvidId}`,
      { responseType: 'text' }
    );
  }

  public modifyPicvid(
    albumId: number,
    picvidId: number,
    request: FormData
  ): Observable<Picvid> {
    return this.httpClient.put<Picvid>(
      `${this.pathService}/${albumId}/picvid/${picvidId}`,
      request
    );
  }
}
