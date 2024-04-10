import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { FormComponent } from './form.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { SessionService } from '../../../../services/session.service';
import { of } from 'rxjs';
import { RouterModule } from '@angular/router';
import { AlbumService } from '../../../album/services/album.service';

describe('FormComponent', () => {
  let component: FormComponent;
  let fixture: ComponentFixture<FormComponent>;
  let sessionService: SessionService;
  let albumService: AlbumService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        FormComponent,
        RouterModule.forRoot([]),
        NoopAnimationsModule,
        HttpClientTestingModule,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    sessionService = TestBed.inject(SessionService);
    albumService = TestBed.inject(AlbumService);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('submit should make FormData and call service', () => {
    //Given
    component['id'] = 106;
    component['form'].setValue({
      name: 'Dixee',
      description: 'La reine des chiennes',
    });

    let mockData = new FormData();
    mockData.append('name', 'Dixee');
    mockData.append('description', 'La reine des chiennes');

    const albumServiceSpy = jest
      .spyOn(albumService, 'create')
      .mockReturnValue(of('useless'));

    //When
    component.submit();

    //Then
    expect(albumServiceSpy).toHaveBeenCalledWith(106, mockData);
  });
});
