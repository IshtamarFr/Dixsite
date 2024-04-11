import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { Router, RouterModule } from '@angular/router';
import { MainAlbumCardComponent } from './main-album-card.component';
import { Album } from '../../interfaces/album.interface';
import { AlbumService } from '../../../album/services/album.service';
import { of } from 'rxjs';

describe('MainAlbumCardComponent', () => {
  let component: MainAlbumCardComponent;
  let fixture: ComponentFixture<MainAlbumCardComponent>;
  let albumService: AlbumService;
  let router: Router;

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
        RouterModule.forRoot([]),
        NoopAnimationsModule,
        HttpClientTestingModule,
      ],
      providers: [],
    }).compileComponents();

    fixture = TestBed.createComponent(MainAlbumCardComponent);
    component = fixture.componentInstance;
    component.album = mockAlbum;
    component.userId = 1;
    fixture.detectChanges();
    albumService = TestBed.inject(AlbumService);
    router = TestBed.inject(Router);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('getImageUrl should return correct image URL', () => {
    //Given

    //When-Then
    expect(component.getImageURL('lala.jpg')).toBe('api/file/crop-lala.jpg');
  });

  it('toggle change should changestatus from OFFLINE to ONLINE and reflect it', () => {
    //Given
    let albumServiceSpy = jest
      .spyOn(albumService, 'changeStatus')
      .mockReturnValue(of('useless'));

    //When
    component.onOwnToggleChange(mockAlbum);

    //Then
    expect(albumServiceSpy).toHaveBeenCalledWith(1, 999, 'ONLINE');
    expect(component.album.status).toBe('ONLINE');
  });

  it('open an album should call the router', () => {
    //Given
    let routerSpy = jest.spyOn(router, 'navigate');

    //When
    component.openAlbum(mockAlbum);

    //Then
    expect(routerSpy).toHaveBeenCalledWith(['/album', 999]);
  });

  it('unsubscribe an album should call service and call for refreshed subscribed list', () => {
    //Given
    let albumServiceSpyUnsubscribe = jest
      .spyOn(albumService, 'unSubscribeToAlbum')
      .mockReturnValue(of('anything'));

    //When
    component.onUnsubscribe(mockAlbum);

    //Then
    expect(albumServiceSpyUnsubscribe).toHaveBeenCalledWith(1, 999);
  });
});
