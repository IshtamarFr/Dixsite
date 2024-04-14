import { Component, NgZone, OnInit } from '@angular/core';
import { MenuComponent } from '../../../../components/menu/menu.component';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { Album } from '../../../main/interfaces/album.interface';
import { AlbumService } from '../../services/album.service';
import { take } from 'rxjs';
import { SessionService } from '../../../../services/session.service';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { CommonModule } from '@angular/common';
import { Picvid } from '../../interfaces/picvid.interface';
import { PicvidService } from '../../services/picvid.service';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { FormsModule } from '@angular/forms';
import { DialogService } from '../../../../utils/dialog.service';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { CustomMatPaginator } from '../../../../interfaces/custom-mat-paginator.interface';
import { AlbumTitleComponent } from '../album-title/album-title.component';
import { MatCardModule } from '@angular/material/card';

@Component({
  selector: 'app-album-list',
  standalone: true,
  imports: [
    CommonModule,
    MenuComponent,
    MatIconModule,
    MatButtonModule,
    MatSlideToggleModule,
    FormsModule,
    RouterModule,
    MatPaginatorModule,
    AlbumTitleComponent,
    MatCardModule,
  ],
  templateUrl: './album-list.component.html',
  styleUrl: './album-list.component.scss',
})
export class AlbumListComponent implements OnInit {
  public albumId: number = 0;
  public album?: Album;
  public isAdmin: boolean = false;
  public isModo: boolean = false;
  public isAdminPanelShown: boolean = false;
  public picvids: Picvid[] = [];

  public paginator: CustomMatPaginator = {
    pageSize: 20,
    pageIndex: 0,
    showFirstLastButtons: true,
  };

  constructor(
    private activatedRoute: ActivatedRoute,
    private albumService: AlbumService,
    private sessionService: SessionService,
    private picvidService: PicvidService,
    private dialogService: DialogService,
    private ngZone: NgZone,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.albumId = this.activatedRoute.snapshot.params['albumId'];
    this.initAllPicvids();
    this.picvidService
      .getAllPicvidsByAlbumId(this.albumId)
      .pipe(take(1))
      .subscribe({
        next: (resp) =>
          (this.picvids = resp.sort((a, b) =>
            this.bestDate(a) > this.bestDate(b) ? -1 : 1
          )),
      });
  }

  initAllPicvids(): void {
    this.albumService
      .getAlbumById(this.albumId)
      .pipe(take(1))
      .subscribe({
        next: (resp) => {
          this.album = resp;
          if (this.sessionService.user) {
            if (
              this.sessionService.user.roles.includes('ADMIN') ||
              this.sessionService.user.id === this.album?.owner_id
            ) {
              this.isAdmin = true;
              this.isModo = true;
            }

            if (this.album?.moderator_ids.includes(this.sessionService.user.id))
              this.isModo = true;
          }
        },
        error: () => {
          this.ngZone.run(() => {
            this.router.navigate(['/main']);
          });
        },
      });
  }

  bestDate(picvid: Picvid): Date {
    if (picvid.dateTime) {
      return picvid.dateTime;
    } else if (picvid.dateTimeExif) {
      return picvid.dateTimeExif;
    } else {
      return picvid.createdAt;
    }
  }

  getImageURL(imageId: string): string {
    return this.albumService.getImageURL(imageId);
  }

  goToPicvid(picvid: Picvid): void {
    this.router.navigate(['picvid', picvid.id], {
      relativeTo: this.activatedRoute,
    });
  }

  deletePicvid(picvid: Picvid): void {
    this.dialogService
      .openConfirmDialog('Voulez-vous vraiment supprimer cet élément ?', true)
      .subscribe({
        next: (confirmed) => {
          if (confirmed) {
            this.picvidService
              .deletePicvidById(this.albumId, picvid.id)
              .pipe(take(1))
              .subscribe({
                next: (_) => {
                  this.picvids = this.picvids.filter((x) => x.id !== picvid.id);
                },
              });
          }
        },
      });
  }

  handlePageEvent(e: PageEvent) {
    this.paginator.pageSize = e.pageSize;
    this.paginator.pageIndex = e.pageIndex;
  }

  back(): void {
    window.history.back();
  }
}
