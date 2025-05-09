import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OverwriteCommandlineParamsModalComponent } from './overwrite-commandline-params-modal.component';

describe('OverwriteCommandlineParamsModalComponent', () => {
  let component: OverwriteCommandlineParamsModalComponent;
  let fixture: ComponentFixture<OverwriteCommandlineParamsModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OverwriteCommandlineParamsModalComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(OverwriteCommandlineParamsModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
