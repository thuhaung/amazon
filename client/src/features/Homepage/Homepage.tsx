import "./Homepage.scss";
import { useRef, useState, useEffect } from "react";
import hero from "../../assets/mobile-hero.png";
import { Layout } from "../../components/Layout/Layout";

export const Homepage = () => {
    const carouselRef = useRef<null | HTMLDivElement>(null);
    const [translateX, setTranslateX] = useState(0);
    const [currentTopic, setCurrentTopic] = useState(1);

    const topicCarousel = [
        {
            id: "books", 
            title: "Shop books",
            color: "sage",
            img: "https://www.southernliving.com/thmb/WYR3KLFcxNQ1MuZNVTzDe0ku33A=/1500x0/filters:no_upscale():max_bytes(150000):strip_icc()/gettyimages-183822187-1-709a5ded972a426a9e214eba1f81c8a4.jpg"
        },
        {
            id: "gaming",
            title: "Essentials for Gamers",
            color: "purple",
            img: "https://media.wired.com/photos/61f48f02d0e55ccbebd52d15/master/pass/Gear-Rant-Game-Family-Plans-1334436001.jpg"
        },
        {
            id: "cooking",
            title: "Kitchen favorites",
            color: "beige",
            img: "https://www.foodandwine.com/thmb/O2ZjV3L_cNcz258bkrDgrYPanD0=/1500x0/filters:no_upscale():max_bytes(150000):strip_icc()/What-Is-Chaos-Cooking-TikToks-Latest-Obsession-FT-BLOG0723-be130eb8cffe400eb13ccfbe47de733a.jpg"
        },
        {
            id: "beauty",
            title: "Beauty products",
            color: "pink",
            img: "https://a57.foxnews.com/static.foxnews.com/foxnews.com/content/uploads/2023/10/1200/675/Commerce-Amazon-Beauth-Haul-2023-iStock-1488453249.png?ve=1&tl=1"
        }
    ];

    const onClickNext = () => {
        const offset = carouselRef.current!.scrollWidth/4;

        if (currentTopic === topicCarousel.length) {
            setTranslateX(0);
            setCurrentTopic(1);
        }
        else {
            setTranslateX(-offset * currentTopic);
            setCurrentTopic(prev => prev + 1);
        }
    }

    const onClickPrev = () => {
        const offset = carouselRef.current!.scrollWidth/4;

        if (currentTopic === 1) {
            setTranslateX(-offset * (topicCarousel.length - 1));
            setCurrentTopic(topicCarousel.length);
        }
        else {
            setTranslateX(-offset * (currentTopic - 2));
            setCurrentTopic(prev => prev - 1);
        }
    }

    return (
        <Layout title="Amazon, Spend Less, Smile More">
            <div className="homepage">
                <div className="main-content">
                    <div className="topic-carousel">
                        <div className="button prev" onClick={onClickPrev}>
                            {"<"}
                        </div>
                        <div className="topics" 
                            ref={carouselRef}
                            style={{transform: `translate(${translateX}px, 0)`}}>
                            {
                                topicCarousel.map(topic => 
                                    <div className={"topic " + topic.color} key={topic.id}>
                                        <div className="title">
                                            {topic.title}
                                        </div>
                                        <img
                                            src={topic.img}
                                            alt={topic.title} />  
                                    </div>
                                )
                            }
                        </div>
                        <div className="button next" onClick={onClickNext}>
                            {">"}
                        </div>
                    </div>
                    <div className="mobile-hero">
                        <img
                            src={hero}
                            alt="Shop Amazon devices with Alexa" />
                    </div>
                </div>
            </div>
        </Layout>
    );
}