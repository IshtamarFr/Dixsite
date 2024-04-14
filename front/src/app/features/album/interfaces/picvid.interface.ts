export interface Picvid {
  id: number;
  name: string;
  dateTime?: Date;
  dateTimeExif?: Date;
  takenLocation: string;
  fileLocation: string;
  description: string;
  createdAt: Date;
  modifiedAt: Date;
  album_id: number;
}
