import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, Input, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { Comment } from '../../../album/interfaces/comment.interface';
import { MatCardModule } from '@angular/material/card';
import { PicvidService } from '../../../album/services/picvid.service';
import { Picvid } from '../../../album/interfaces/picvid.interface';
import { take } from 'rxjs';

@Component({
  selector: 'app-mycomment',
  standalone: true,
  imports: [CommonModule, MatCardModule, RouterModule],
  templateUrl: './mycomment.component.html',
  styleUrl: './mycomment.component.scss',
})
export class MycommentComponent implements OnInit {
  @Input() comment!: Comment;
  picvid?: Picvid;

  constructor(
    private httpClient: HttpClient,
    private router: Router,
    private picvidService: PicvidService
  ) {}

  ngOnInit(): void {
    this.picvidService
      .getPicvidById(this.comment.album_id, this.comment.picvid_id)
      .pipe(take(1))
      .subscribe({
        next: (resp) => {
          this.picvid = resp;
        },
      });
  }

  getImageURL(picvidId: string): string {
    return this.picvidService.getImageURL(picvidId!);
  }
}
