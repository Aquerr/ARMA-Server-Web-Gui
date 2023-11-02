import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MobileHeaderComponent } from './mobile-header.component';

describe('HeaderComponent', () => {
  let component: MobileHeaderComponent;
  let fixture: ComponentFixture<MobileHeaderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MobileHeaderComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MobileHeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
