import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Comment } from '../interfaces/comment.interface';
import { CreateCommentRequest } from '../interfaces/create-comment-request.interface';
import { MyCommentsResponse } from '../../main/interfaces/my-comments-response.interface';

@Injectable({
  providedIn: 'root',
})
export class CommentService {
  private pathService = 'api/album';

  constructor(private httpClient: HttpClient) {}

  public getAllCommentsByPicvidId(
    albumId: number,
    picvidId: number
  ): Observable<Comment[]> {
    return this.httpClient.get<Comment[]>(
      `${this.pathService}/${albumId}/picvid/${picvidId}/comment`
    );
  }

  public createComment(
    albumId: number,
    picvidId: number,
    request: CreateCommentRequest
  ): Observable<string> {
    return this.httpClient.post(
      `${this.pathService}/${albumId}/picvid/${picvidId}/comment`,
      request,
      { responseType: 'text' }
    );
  }

  public getAllCommentsByUserId(id: number): Observable<MyCommentsResponse> {
    return this.httpClient.get<MyCommentsResponse>(`api/user/${id}/comments`);
  }
}
