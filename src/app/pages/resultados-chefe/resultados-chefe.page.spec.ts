import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ResultadosChefePage } from './resultados-chefe.page';

describe('ResultadosChefePage', () => {
  let component: ResultadosChefePage;
  let fixture: ComponentFixture<ResultadosChefePage>;

  beforeEach(() => {
    fixture = TestBed.createComponent(ResultadosChefePage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
