import { TestBed } from '@angular/core/testing';

import { AlbumService } from './album.service';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';

describe('AlbumService', () => {
  let service: AlbumService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    service = TestBed.inject(AlbumService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('create new album should call service', () => {
    //Given

    //When
    service.create(42, new FormData()).subscribe();

    //Then
    const req = httpTestingController.expectOne(
      service['pathService'] + '/42/album'
    );
    expect(req.request.method).toEqual('POST');
    httpTestingController.verify();
  });

  it('change status should call service', () => {
    //Given

    //When
    service.changeStatus(42, 999, 'ONLINE').subscribe();

    //Then
    const req = httpTestingController.expectOne(
      service['pathService'] + '/42/album/999/status'
    );
    expect(req.request.method).toEqual('PUT');
    httpTestingController.verify();
  });

  it('subscribe should call service', () => {
    //Given

    //When
    service.subscribeToAlbum(42, 999).subscribe();

    //Then
    const req = httpTestingController.expectOne(
      service['pathService'] + '/42/album/999/subscribe'
    );
    expect(req.request.method).toEqual('POST');
    httpTestingController.verify();
  });

  it('unsubscribe should call service', () => {
    //Given

    //When
    service.unSubscribeToAlbum(42, 999).subscribe();

    //Then
    const req = httpTestingController.expectOne(
      service['pathService'] + '/42/album/999/subscribe'
    );
    expect(req.request.method).toEqual('DELETE');
    httpTestingController.verify();
  });
});
