import { ComponentFixture, TestBed } from '@angular/core/testing';

import { JobViewComponent } from './job-view.component';

describe('JobViewComponent', () => {
  let component: JobViewComponent;
  let fixture: ComponentFixture<JobViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [JobViewComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(JobViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
