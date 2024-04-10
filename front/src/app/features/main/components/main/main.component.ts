import { Component, OnInit } from '@angular/core';
import { MenuComponent } from '../../../../components/menu/menu.component';
import { SessionService } from '../../../../services/session.service';
import { User } from '../../../../interfaces/user.interface';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { Album } from '../../interfaces/album.interface';
import { AlbumService } from '../../../album/services/album.service';
import { take } from 'rxjs';
import { CommonModule } from '@angular/common';
import { AlbumRequestResponse } from '../../interfaces/albums-request-response.interface';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MainAlbumCardComponent } from '../main-album-card/main-album-card.component';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-main',
  standalone: true,
  imports: [
    CommonModule,
    MenuComponent,
    MatButtonModule,
    MatIconModule,
    MatTooltipModule,
    MainAlbumCardComponent,
    RouterModule,
  ],
  templateUrl: './main.component.html',
  styleUrl: './main.component.scss',
})
export class MainComponent implements OnInit {
  ownedAlbums: Album[] = [];
  subscribedAlbums: Album[] = [];
  user?: User;
  userId: number = 0;

  constructor(
    private sessionService: SessionService,
    private albumService: AlbumService
  ) {}

  ngOnInit() {
    this.user = this.sessionService.user;
    if (this.user) {
      this.userId = this.user.id;
      this.albumService
        .list(this.user.id)
        .pipe(take(1))
        .subscribe({
          next: (resp: AlbumRequestResponse) => {
            this.ownedAlbums = resp.owned;
            this.subscribedAlbums = resp.subscribed;
          },
        });
    }
  }

  onUnsubscribe(albumId: number): void {
    this.subscribedAlbums = this.subscribedAlbums.filter(
      (x) => x.id != albumId
    );
  }
}
