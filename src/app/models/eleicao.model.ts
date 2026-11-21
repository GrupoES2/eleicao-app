export interface Eleicao {
  id: string | number;
  cargo: string;
  data: string;
  turno: string;
  horarioAbertura: string;
  horarioEncerramento: string;
  status: 'AGENDADA' | 'ABERTA' | 'ENCERRADA';
}