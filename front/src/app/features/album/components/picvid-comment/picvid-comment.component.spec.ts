import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { PicvidCommentComponent } from './picvid-comment.component';
import { RouterModule } from '@angular/router';
import { Comment } from '../../interfaces/comment.interface';

describe('PicVidCommentComponent', () => {
  let component: PicvidCommentComponent;
  let fixture: ComponentFixture<PicvidCommentComponent>;

  let mockComment: Comment = {
    id: 16,
    content: 'Ma chérie',
    status: 'ONLINE',
    createdAt: new Date('2024-03-27T20:04:27.858915'),
    owner_id: 1,
    owner_name: 'Ishta',
    picvid_id: 381,
    album_id: 13,
    subcomments: [
      {
        id: 27,
        content: 'Lol',
        status: 'ONLINE',
        createdAt: new Date('2024-03-28T01:35:36.401999'),
        owner_id: 1,
        owner_name: 'Ishta',
        picvid_id: 381,
        album_id: 13,
      },
      {
        id: 28,
        content: "Je t'aime",
        status: 'ONLINE',
        createdAt: new Date('2024-03-28T01:36:02.346066'),
        owner_id: 1,
        owner_name: 'Ishta',
        picvid_id: 381,
        album_id: 13,
      },
    ],
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterModule.forRoot([]),
        NoopAnimationsModule,
        HttpClientTestingModule,
      ],
      providers: [],
    }).compileComponents();

    fixture = TestBed.createComponent(PicvidCommentComponent);
    component = fixture.componentInstance;
    component.albumId = 1;
    component.picvidId = 42;
    component.comment = mockComment;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
