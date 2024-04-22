import { Component, NgZone, OnDestroy, OnInit } from '@angular/core';
import { MenuComponent } from '../../../../components/menu/menu.component';
import { ActivatedRoute, Router } from '@angular/router';
import { Picvid } from '../../interfaces/picvid.interface';
import { PicvidService } from '../../services/picvid.service';
import { Observable, Subscription, fromEvent, take } from 'rxjs';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { AppSettings } from '../../../../utils/app-settings';
import { Comment } from '../../interfaces/comment.interface';
import { CommentService } from '../../services/comment.service';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import {
  FormBuilder,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { CreateCommentRequest } from '../../interfaces/create-comment-request.interface';
import { PicvidCommentComponent } from '../picvid-comment/picvid-comment.component';
import { PicvidTitleComponent } from '../picvid-title/picvid-title.component';
import { AlbumService } from '../../services/album.service';
import { SessionService } from '../../../../services/session.service';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';

@Component({
  selector: 'app-picvid',
  standalone: true,
  imports: [
    MenuComponent,
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatInputModule,
    FormsModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    PicvidCommentComponent,
    PicvidTitleComponent,
    MatSlideToggleModule,
  ],
  templateUrl: './picvid.component.html',
  styleUrl: './picvid.component.scss',
})
export class PicvidComponent implements OnInit, OnDestroy {
  public picvidId: number = 0;
  public albumId: number = 0;
  public picvid!: Picvid;
  public myId?: number = 0;

  public isModo: boolean = false;
  public isAdminPanelShown: boolean = false;

  screenWidth: number = window.innerWidth;
  resizeObservable$!: Observable<Event>;
  resizeSubscription$!: Subscription;
  MIN_PC_SIZE = AppSettings.MIN_PC_SIZE;

  public comments: Comment[] = [];

  public form = this.fb.group({
    content: ['', [Validators.required, Validators.maxLength(255)]],
  });

  public constructor(
    private activatedRoute: ActivatedRoute,
    private picvidService: PicvidService,
    private commentService: CommentService,
    private fb: FormBuilder,
    private albumService: AlbumService,
    private router: Router,
    private ngZone: NgZone,
    private sessionService: SessionService
  ) {}

  ngOnInit(): void {
    this.albumId = this.activatedRoute.snapshot.params['albumId'];
    this.picvidId = this.activatedRoute.snapshot.params['picvidId'];

    this.initAllPicvids();
    this.picvidService
      .getPicvidById(this.albumId, this.picvidId)
      .pipe(take(1))
      .subscribe({
        next: (resp) => {
          this.picvid = resp;
        },
      });

    this.resizeObservable$ = fromEvent(window, 'resize');
    this.resizeSubscription$ = this.resizeObservable$.subscribe(() => {
      this.screenWidth = window.innerWidth;
    });

    this.refreshComments();
  }

  initAllPicvids(): void {
    this.albumService
      .getAlbumById(this.albumId)
      .pipe(take(1))
      .subscribe({
        next: (resp) => {
          if (this.sessionService.user) {
            if (
              this.sessionService.user.roles.includes('ADMIN') ||
              this.sessionService.user.id === resp.owner_id
            ) {
              this.isModo = true;
            }

            if (resp.moderator_ids.includes(this.sessionService.user.id))
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

  ngOnDestroy(): void {
    this.resizeSubscription$.unsubscribe();
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

  screenClass(width: number): string {
    return width > this.MIN_PC_SIZE ? 'picvidGeneral' : 'picvidMobile';
  }

  showImage(fileCode: string): string {
    return this.picvidService.getFullPicvidURL(fileCode);
  }

  refreshComments(commentId?: number): void {
    this.commentService
      .getAllCommentsByPicvidId(this.albumId, this.picvidId)
      .pipe(take(1))
      .subscribe((resp) => {
        this.comments = resp;
      });
  }

  submit(): void {
    let request: CreateCommentRequest = {
      content: this.form.get('content')?.value!,
    };

    this.commentService
      .createComment(this.albumId, this.picvidId, request)
      .pipe(take(1))
      .subscribe({
        next: (_) => {
          this.refreshComments();
          this.form.get('content')?.reset();
        },
      });
  }

  isPicvidVid(picvid: Picvid): boolean {
    return this.picvidService.isPicvidVid(picvid.fileLocation);
  }

  back(): void {
    window.history.back();
  }
}
