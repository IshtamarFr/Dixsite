import { Component, OnInit } from '@angular/core';
import { AlbumService } from '../../../album/services/album.service';
import { SessionService } from '../../../../services/session.service';
import { Router, RouterLink } from '@angular/router';
import { User } from '../../../../interfaces/user.interface';
import { Album } from '../../interfaces/album.interface';
import { map, take } from 'rxjs/operators';
import { MenuComponent } from '../../../../components/menu/menu.component';
import { UtilityService } from '../../../../utils/utility.service';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { AlbumRequestResponse } from '../../interfaces/albums-request-response.interface';
import { MatIconModule } from '@angular/material/icon';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { CustomMatPaginator } from '../../../../interfaces/custom-mat-paginator.interface';

@Component({
  selector: 'app-albums',
  standalone: true,
  imports: [
    RouterLink,
    MenuComponent,
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatPaginator,
  ],
  templateUrl: './albums.component.html',
  styleUrl: './albums.component.scss',
})
export class AlbumsComponent implements OnInit {
  user?: User;
  userId: number = 0;
  albums: Album[] = [];
  subscribedAlbums: number[] = [];
  pageIndex: number = 0;

  public paginator: CustomMatPaginator = {
    pageSize: 20,
    pageIndex: 0,
    showFirstLastButtons: true,
  };

  dateFormat: string = 'dd/MM/yyyy HH:mm';

  constructor(
    private albumService: AlbumService,
    private sessionService: SessionService,
    private utilityService: UtilityService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.user = this.sessionService.user;
    if (this.user) this.userId = this.user.id;
    if (window.location.href.includes('/en/'))
      this.dateFormat = 'MM/dd/yyyy h:mm a';

    this.albumService
      .list(this.userId)
      .pipe(take(1))
      .subscribe({
        next: (resp: AlbumRequestResponse) => {
          this.subscribedAlbums = resp.subscribed.map((album) => album.id);
        },
      });
    this.albumService
      .listAll()
      .pipe(
        take(1),
        map((albums) =>
          albums.filter((album) => album.owner_id !== this.userId)
        ),
        map((albums) => this.utilityService.shuffle(albums))
      )
      .subscribe({
        next: (resp: Album[]) => {
          this.albums = resp;
        },
      });
  }

  min(a: number, b: number): number {
    return Math.min(a, b);
  }

  getImageURL(imageId: string): string {
    return this.albumService.getImageURL(imageId);
  }

  onUnsubscribe(album: Album): void {
    this.albumService
      .unSubscribeToAlbum(this.userId, album.id)
      .pipe(take(1))
      .subscribe({
        next: () => {
          this.subscribedAlbums = this.subscribedAlbums.filter(
            (x) => x != album.id
          );
        },
      });
  }

  onSubscribe(album: Album): void {
    this.albumService
      .subscribeToAlbum(this.userId, album.id)
      .pipe(take(1))
      .subscribe({
        next: () => {
          this.subscribedAlbums.push(album.id);
        },
      });
  }

  openAlbum(album: Album): void {
    this.router.navigate(['/album', album.id]);
  }

  handlePageEvent(e: PageEvent) {
    this.paginator.pageSize = e.pageSize;
    this.paginator.pageIndex = e.pageIndex;
  }

  back(): void {
    window.history.back();
  }
}
