export interface Comment {
  id: number;
  content: string;
  status: string;
  createdAt: Date;
  owner_id: number;
  owner_name: string;
  picvid_id: number;
  album_id: number;
  mother_id?: number;
  subcomments?: Comment[];
}
