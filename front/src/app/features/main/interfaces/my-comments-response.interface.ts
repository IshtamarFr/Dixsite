import { Comment } from '../../album/interfaces/comment.interface';

export interface MyCommentsResponse {
  myComments: Comment[];
  myAlbumsComments: Comment[];
}
