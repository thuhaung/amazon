import "./Spinner.scss";

export const Spinner = ({ style }: {style?: React.CSSProperties}) => {
    return (
        <div className="ring" style={style}>
            <div></div>
            <div></div>
            <div></div>
            <div></div>
        </div>
    );
}