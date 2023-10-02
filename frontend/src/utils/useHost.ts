export function useHost() {
    const wrapHost = (imageUrl: string) => {
        return `${window.location.origin.split(':')[1]}:19000/spbg/${imageUrl}`;
    }
    return {
        wrapHost
    }
}
