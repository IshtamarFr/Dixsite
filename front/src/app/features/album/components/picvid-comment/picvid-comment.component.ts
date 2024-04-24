import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatExpansionModule } from '@angular/material/expansion';
import { Comment } from '../../interfaces/comment.interface';
import { SessionService } from '../../../../services/session.service';
import { MatButtonModule } from '@angular/material/button';
import {
  FormBuilder,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { take } from 'rxjs';
import { CreateCommentRequest } from '../../interfaces/create-comment-request.interface';
import { CommentService } from '../../services/comment.service';
import { MatInputModule } from '@angular/material/input';

@Component({
  selector: 'app-picvid-comment',
  standalone: true,
  imports: [
    MatExpansionModule,
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    FormsModule,
    ReactiveFormsModule,
    MatInputModule,
  ],
  templateUrl: './picvid-comment.component.html',
  styleUrl: './picvid-comment.component.scss',
})
export class PicvidCommentComponent implements OnInit {
  @Input() comment!: Comment;
  @Input() albumId!: number;
  @Input() picvidId!: number;
  @Input() isModerating: boolean = false;

  @Output() commentEvent = new EventEmitter<number>();

  public myId?: number;
  public subcomments: Comment[] = [];

  public dateFormat = 'dd/MM/yyyy HH:mm';

  public form = this.fb.group({
    content: ['', [Validators.required, Validators.maxLength(255)]],
  });

  public constructor(
    private sessionService: SessionService,
    private commentService: CommentService,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    if (window.location.href.includes('/en/'))
      this.dateFormat = 'MM/dd/yyyy h:mm a';

    this.myId = this.sessionService.user?.id;
    if (this.comment.subcomments) this.subcomments = this.comment.subcomments;
  }

  submit(): void {
    let request: CreateCommentRequest = {
      content: this.form.get('content')?.value!,
      mother_id: this.comment.id,
    };

    this.commentService
      .createComment(this.albumId, this.picvidId, request)
      .pipe(take(1))
      .subscribe({
        next: (_) => {
          this.form.get('content')?.reset();
          this.commentEvent.emit(this.comment.id);
        },
      });
  }
}
