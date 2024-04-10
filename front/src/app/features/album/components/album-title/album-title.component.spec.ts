import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { AlbumListComponent } from '../album-list/album-list.component';
import { AlbumTitleComponent } from './album-title.component';
import { RouterModule } from '@angular/router';
import { Album } from '../../../main/interfaces/album.interface';

describe('AlbumTitleComponent', () => {
  let component: AlbumTitleComponent;
  let fixture: ComponentFixture<AlbumTitleComponent>;

  const mockAlbum: Album = {
    id: 999,
    name: 'Dixee',
    description: 'La chienne des reines',
    homePicture: null,
    status: 'OFFLINE',
    owner_id: 1,
    owner_name: 'scp682 evil',
    createdAt: new Date(),
    modifiedAt: new Date(),
    moderator_ids: [],
    moderator_names: [],
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        AlbumListComponent,
        NoopAnimationsModule,
        HttpClientTestingModule,
        RouterModule.forRoot([]),
      ],
      providers: [],
    }).compileComponents();

    fixture = TestBed.createComponent(AlbumTitleComponent);
    component = fixture.componentInstance;
    component.album = mockAlbum;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
