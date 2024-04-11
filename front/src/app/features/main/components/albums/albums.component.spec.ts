import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { SessionService } from '../../../../services/session.service';
import { AlbumsComponent } from './albums.component';
import { Album } from '../../interfaces/album.interface';
import { of } from 'rxjs';
import { Router, RouterModule } from '@angular/router';
import { AlbumService } from '../../../album/services/album.service';

describe('AlbumsComponent', () => {
  let component: AlbumsComponent;
  let fixture: ComponentFixture<AlbumsComponent>;
  let sessionService: SessionService;
  let albumService: AlbumService;
  let router: Router;

  const mockAlbum: Album = {
    id: 999,
    name: 'Dixee',
    description: 'La chienne des reines',
    homePicture: null,
    status: 'ONLINE',
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
        AlbumsComponent,
        RouterModule.forRoot([]),
        NoopAnimationsModule,
        HttpClientTestingModule,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(AlbumsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    sessionService = TestBed.inject(SessionService);
    albumService = TestBed.inject(AlbumService);
    router = TestBed.inject(Router);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('subscribe to album should call service', () => {
    //Given
    component['userId'] = 42;
    component['subscribedAlbums'] = [17];
    let albumServiceSpy = jest
      .spyOn(albumService, 'subscribeToAlbum')
      .mockReturnValue(of('anything'));

    //When
    component.onSubscribe(mockAlbum);

    //Then
    expect(albumServiceSpy).toHaveBeenCalledWith(42, mockAlbum.id);
    expect(component['subscribedAlbums']).toContain(17);
    expect(component['subscribedAlbums']).toContain(999);
  });

  it('unsubscribe to album should call service', () => {
    //Given
    component['userId'] = 42;
    component['subscribedAlbums'] = [17, 999, 10000];
    let albumServiceSpy = jest
      .spyOn(albumService, 'unSubscribeToAlbum')
      .mockReturnValue(of('anything'));

    //When
    component.onUnsubscribe(mockAlbum);

    //Then
    expect(albumServiceSpy).toHaveBeenCalledWith(42, mockAlbum.id);
    expect(component['subscribedAlbums']).toContain(17);
    expect(component['subscribedAlbums']).not.toContain(999);
    expect(component['subscribedAlbums']).toContain(10000);
  });

  it('min works', () => {
    expect(component.min(3, 7)).toBe(3);
    expect(component.min(-5, -13)).toBe(-13);
  });

  it('open an album should call the router', () => {
    //Given
    let routerSpy = jest.spyOn(router, 'navigate');

    //When
    component.openAlbum(mockAlbum);

    //Then
    expect(routerSpy).toHaveBeenCalledWith(['/album', 999]);
  });
});
