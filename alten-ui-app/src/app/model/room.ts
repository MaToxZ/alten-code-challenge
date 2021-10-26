export interface Room{
    id: number;
    roomView: string;
    roomType: {
        id: number;
        name?: string;
        occupants?: number;
        beds?: [
            {
                quantity: number;
                bedType: string
            },
            {
                quantity: number;
                bedType: string
            }
        ]
    }
    status?: number;
}