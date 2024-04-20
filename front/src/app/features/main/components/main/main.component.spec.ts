import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MainComponent } from './main.component';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Router, RouterModule } from '@angular/router';
import { AlbumService } from '../../../album/services/album.service';
import { User } from '../../../../interfaces/user.interface';
import '@angular/localize/init';

describe('MainComponent', () => {
  let component: MainComponent;
  let fixture: ComponentFixture<MainComponent>;
  let albumService: AlbumService;
  let router: Router;

  let mockUser: User = {
    id: 42,
    email: 'test@test.com',
    name: 'tester',
    roles: 'ROLE_USER',
    maxAlbums: 10,
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        MainComponent,
        RouterModule.forRoot([]),
        NoopAnimationsModule,
        HttpClientTestingModule,
      ],
      providers: [AlbumService],
    }).compileComponents();

    fixture = TestBed.createComponent(MainComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    albumService = TestBed.inject(AlbumService);
    router = TestBed.inject(Router);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
