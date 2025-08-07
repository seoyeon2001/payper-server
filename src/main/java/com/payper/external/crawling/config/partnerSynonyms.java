package com.payper.external.crawling.config;

import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

public class partnerSynonyms {

    public static final Map<String, List<String>> ps = Map.ofEntries(
            // 편의점
            entry("GS25", List.of("gs25", "GS", "gs")),
            entry("CU", List.of("cu")),
            entry("세븐일레븐", List.of("7eleven", "세븐일레븐편의점")),
            entry("이마트24", List.of("emart24", "이마트24")),
            entry("미니스톱", List.of("ministop", "미니스탑")),

            // 마트/대형할인점
            entry("이마트", List.of("emart")),
            entry("롯데마트", List.of("lottemart")),
            entry("홈플러스", List.of("homeplus")),
            entry("코스트코", List.of("costco")),
            entry("트레이더스", List.of("traders")),
            entry("노브랜드", List.of("nobrand", "노브랜드이마트")),
            entry("GS더프레시", List.of("gs the fresh", "gs더프레시")),
            entry("롯데슈퍼", List.of("lotte super")),
            entry("하나로마트", List.of("hanaro mart", "농협 하나로마트")),
            entry("홈플러스익스프레스", List.of("homeplus express")),
            entry("이마트에브리데이", List.of("emart everyday")),

            // 카페/베이커리
            entry("스타벅스", List.of("starbucks", "스벅", "스타박스", "스타벅스커피")),
            entry("투썸플레이스", List.of("투썸", "twosome", "투썸플", "twosomeplace")),
            entry("이디야커피", List.of("이디야", "ediya")),
            entry("파리바게뜨", List.of("파바", "파리바게트", "parisbagutte")),
            entry("뚜레쥬르", List.of("뚜레주르", "tresure", "뚜레쥬르")),
            entry("커피빈", List.of("coffeebean", "커피빈즈")),
            entry("폴바셋", List.of("paulbassett", "폴 바셋", "폴바셋")),
            entry("엔제리너스", List.of("angelinus", "엔제리너스", "엔젤리너스")),
            entry("빽다방", List.of("paik's coffee", "빽다방")),
            entry("메가커피", List.of("mega coffee")),
            entry("할리스커피", List.of("hallys coffee", "할리스", "hallys")),
            entry("탐앤탐스", List.of("tomntoms")),
            entry("던킨도너츠", List.of("dunkin", "던킨")),
            entry("배스킨라빈스", List.of("baskin robbins", "배라", "배스킨", "베라", "베스킨라빈스", "베스킨로빈스", "배스킨로빈스")),
            entry("크리스피크림도넛", List.of("krispy kreme", "크리스피")),
            entry("파스쿠찌", List.of("pascucci", "파스꾸찌")),
            entry("카페베네", List.of("cafebene", "카페배네")),
            entry("공차", List.of("gong cha")),

            // 패스트푸드/외식
            entry("맥도날드", List.of("mcdonalds", "맥도", "맥도날즈", "맥날")),
            entry("버거킹", List.of("burgerking")),
            entry("롯데리아", List.of("lotteria")),
            entry("KFC", List.of("kfc", "케이에프씨")),
            entry("서브웨이", List.of("subway", "섭웨")),
            entry("맘스터치", List.of("mom's touch", "맘터")),
            entry("빕스", List.of("vips")),
            entry("아웃백", List.of("outback", "아웃벡")),
            entry("교촌치킨", List.of("kyochon", "교촌")),
            entry("BHC", List.of("bhc", "비에이치씨")),
            entry("BBQ", List.of("bbq")),
            entry("굽네치킨", List.of("goobne", "굽네")),
            entry("매드포갈릭", List.of("madforgarlic", "메드포갈릭")),
            entry("사보텐", List.of("saboten", "샤보텐")),
            entry("도미노피자", List.of("domino's", "도미노")),
            entry("피자헛", List.of("pizzahut")),
            entry("미스터피자", List.of("mrpizza", "미피")),
            entry("피자알볼로", List.of("pizza alvolo", "알볼로 피자", "알볼로")),
            entry("피자스쿨", List.of("pizza school")),
            entry("한솥도시락", List.of("hansot", "한솥")),
            entry("이삭토스트", List.of("isaac toast", "이삭")),

            // 백화점/쇼핑
            entry("롯데백화점", List.of("lotte department store", "롯데백화점", "롯데", "롯백")),
            entry("현대백화점", List.of("hyundai department store", "현대백화점", "현대", "현백")),
            entry("신세계백화점", List.of("shinsegae department store", "신세계")),
            entry("갤러리아백화점", List.of("galleria department store", "갤러리아")),
            entry("AK플라자", List.of("ak plaza", "ak플라자", "애경", "애경백화점")),
            entry("NC백화점", List.of("nc department store", "nc백화점", "nc", "NC")),
            entry("스타필드", List.of("starfield", "스타필드")),
            entry("신세계프리미엄아울렛", List.of("shinsegae premium outlet", "신세계프리미엄아울랫")),
            entry("롯데프리미엄아울렛", List.of("lotte premium outlet", "롯데프리미엄아울랫")),
            entry("현대프리미엄아울렛", List.of("hyundai premium outlet", "현대프리미엄아울랫")),
            entry("타임스퀘어", List.of("times square", "타임스퀘어")),

            // 주유/자동차
            entry("SK에너지", List.of("sk energy", "sk주유소", "sk에너지")),
            entry("GS칼텍스", List.of("gs caltex", "gs칼텍스")),
            entry("S-Oil", List.of("s oil", "에쓰오일")),
            entry("현대오일뱅크", List.of("hyundai oilbank", "현대오일뱅크")),

            // 여가/문화/뷰티
            entry("아트박스", List.of("artbox")),
            entry("다이소", List.of("daiso")),

            // 영화
            entry("CGV", List.of("cgv")),
            entry("롯데시네마", List.of("lotte cinema")),
            entry("메가박스", List.of("megabox")),

            // 뷰티
            entry("올리브영", List.of("oliveyoung", "올영")),
            entry("시코르", List.of("chicor")),

            // 디지털구독
            entry("넷플릭스", List.of("netflix", "넷플", "냇플릭스")),
            entry("티빙", List.of("tving")),
            entry("왓챠", List.of("watcha", "왓차")),
            entry("웨이브", List.of("wavve")),
            entry("디즈니플러스", List.of("disney+", "디플", "디즈니+")),
            entry("쿠팡플레이", List.of("coupang play", "쿠플")),
            entry("유튜브", List.of("youtube", "유튭")),
            entry("애플뮤직", List.of("apple music", "애플뮤직")),
            entry("멜론", List.of("melon", "메론")),
            entry("FLO", List.of("flo", "플로")),
            entry("지니뮤직", List.of("genie music")),
            entry("밀리의서재", List.of("milli's library", "밀리의 서재")),

            // 온라인쇼핑
            entry("쿠팡", List.of("coupang")),
            entry("11번가", List.of("11st", "일일번가")),
            entry("G마켓", List.of("gmarket", "지마켓")),
            entry("옥션", List.of("auction")),
            entry("SSG.COM", List.of("ssg.com", "쓱닷컴", "쓱")),
            entry("이마트몰", List.of("emart mall", "이마트몰")),
            entry("마켓컬리", List.of("market kurly")),
            entry("인터파크", List.of("interpark")),
            entry("신세계몰", List.of("shinsegae mall")),
            entry("무신사", List.of("musinsa")),
            entry("지그재그", List.of("zigzag", "직잭")),

            // 배달
            entry("배달의민족", List.of("baemin", "배민", "배달의민족", "배달의 민족")),
            entry("요기요", List.of("yogiyo", "요기요")),
            entry("쿠팡이츠", List.of("coupang eats", "쿠팡이츠")),
            entry("땡겨요", List.of("ddanggyo", "땡겨요")),

            // 교통
            entry("카카오택시", List.of("kakao taxi", "카카오택시", "카카오T")),
            entry("우버", List.of("uber", "UBER")),
            entry("타다", List.of("tada", "TADA")),
            entry("쏘카", List.of("socar", "SOCAR")),

            // 통신
            entry("SKT", List.of("sk telecom", "sk텔레콤", "skt")),
            entry("KT", List.of("kt", "케이티")),
            entry("LG유플러스", List.of("lg u+", "유플러스", "lg유플러스")),

            // 서점
            entry("교보문고", List.of("kyobo", "교보")),
            entry("영풍문고", List.of("youngpoong", "영풍")),
            entry("알라딘", List.of("aladin")),
            entry("YES24", List.of("yes24", "예스24")),
            entry("핫트랙스", List.of("hot tracks", "핫트렉스"))
    );


}

