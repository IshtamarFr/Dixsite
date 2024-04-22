import { TestBed } from '@angular/core/testing';

import { PicvidService } from './picvid.service';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';

describe('CommentService', () => {
  let service: PicvidService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    service = TestBed.inject(PicvidService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('when I send a file, it should send correct file extension', () => {
    //Given

    //When-Then
    expect(service.getPicvidExtension('truc.machin.bidule')).toBe('bidule');
  });
});
