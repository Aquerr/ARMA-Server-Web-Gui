import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CdlcComponent } from './cdlc.component';

describe('CdlcComponent', () => {
  let component: CdlcComponent;
  let fixture: ComponentFixture<CdlcComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CdlcComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CdlcComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
