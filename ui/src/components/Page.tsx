export default function page({children }: {children:React.ReactNode}) {
    return (
        <div className="page">
            <div className="container">{children}</div>
            </div>
        );
}