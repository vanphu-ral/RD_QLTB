export interface Column {
  Field: string;
  Header: string;
  IsSearch?: boolean;
  IsHide?: boolean;
  TypeSearch?: 'select' | 'date' | 'text';
  Options?: { label: string; value: any }[];
  style?: { [key: string]: string };
  pipe?: { name: string; args?: string[] };
};