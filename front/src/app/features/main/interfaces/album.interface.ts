export interface Album {
  id: number;
  name: string;
  description: string;
  homePicture: string | null;
  status: string;
  owner_id: number;
  owner_name: string;
  createdAt: Date;
  modifiedAt: Date;
}
