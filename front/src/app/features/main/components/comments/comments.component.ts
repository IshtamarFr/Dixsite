import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { MenuComponent } from '../../../../components/menu/menu.component';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { Comment } from '../../../album/interfaces/comment.interface';
import { SessionService } from '../../../../services/session.service';
import { CommentService } from '../../../album/services/comment.service';
import { MatTabChangeEvent, MatTabsModule } from '@angular/material/tabs';
import { take } from 'rxjs';
import { MycommentComponent } from '../mycomment/mycomment.component';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { Router, RouterModule } from '@angular/router';
import { CustomMatPaginator } from '../../../../interfaces/custom-mat-paginator.interface';

@Component({
  selector: 'app-comments',
  standalone: true,
  imports: [
    CommonModule,
    MenuComponent,
    MatIconModule,
    MatButtonModule,
    MatTabsModule,
    MycommentComponent,
    MatPaginatorModule,
    RouterModule,
  ],
  templateUrl: './comments.component.html',
  styleUrl: './comments.component.scss',
})
export class CommentsComponent implements OnInit {
  public myComments: Comment[] = [];
  public myAnswers: Comment[] = [];
  public myAlbumsComments: Comment[] = [];
  public id?: number;

  public paginator: CustomMatPaginator = {
    pageSize: 20,
    pageIndex: 0,
    showFirstLastButtons: true,
  };

  constructor(
    private sessionService: SessionService,
    private commentService: CommentService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.id = this.sessionService.user?.id;
    this.commentService
      .getAllCommentsByUserId(this.id!)
      .pipe(take(1))
      .subscribe({
        next: (resp) => {
          this.myComments = resp.myComments.filter(
            (comment) => comment.mother_id == null
          );
          this.paginator.length = this.myComments.length;
          this.myAnswers = resp.myComments.filter(
            (comment) => comment.mother_id != null
          );
          this.myAlbumsComments = resp.myAlbumsComments;
        },
      });
  }

  public back() {
    window.history.back();
  }

  goTo(comment: Comment): void {
    this.router.navigate(
      ['album', comment.album_id, 'picvid', comment.picvid_id],
      { fragment: 'comment' + comment.id }
    );
  }

  onTabChange(e: MatTabChangeEvent): void {
    this.paginator.pageIndex = 0;
    switch (e.index) {
      case 0:
        this.paginator.length = this.myComments.length;
        break;
      case 1:
        this.paginator.length = this.myAnswers.length;
        break;
      case 2:
        this.paginator.length = this.myAlbumsComments.length;
    }
  }

  handlePageEvent(e: PageEvent) {
    this.paginator.pageSize = e.pageSize;
    this.paginator.pageIndex = e.pageIndex;
  }
}
