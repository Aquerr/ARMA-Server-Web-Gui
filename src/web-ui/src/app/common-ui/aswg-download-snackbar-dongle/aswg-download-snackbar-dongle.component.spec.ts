import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AswgDownloadSnackbarDongleComponent } from './aswg-download-snackbar-dongle.component';

describe('AswgDownloadSnackbarDongleComponent', () => {
  let component: AswgDownloadSnackbarDongleComponent;
  let fixture: ComponentFixture<AswgDownloadSnackbarDongleComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AswgDownloadSnackbarDongleComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AswgDownloadSnackbarDongleComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
