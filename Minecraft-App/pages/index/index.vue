<template>
  <div class="page-container">
    <!-- 轮播图 -->
    <Carousel :items="carouselList" :interval="3000" :transitionDuration="600" :showArrows="true" :showIndicators="true" :aspectRatio="'16/9'" />

    <!-- 功能导航 -->
    <div class="glass-morphism nav-container">
      <div class="nav-item" @click="navigateTo('/pages/attraction/index')">
        <div class="nav-icon">
          <svg t="1774967500532" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg"
            p-id="6031" width="256" height="256">
            <path
              d="M568.074 954.878l-5.845-23.73c0 0-66.445-217.909-150.15-389.469 59.057-165.185 139.96-384.977 163.93-417.841C615.053 70.292 725.533-16.31 822.863 277.232c121.78 367.142 187.22 677.646 187.22 677.646L568.074 954.878zM747.328 232.189c-39.68-97.332-74.965-124.877-112.8-102.695-37.84 22.227-95.445 248.594-95.445 248.594s34.265-28.56 64.52-30.88c30.25-2.32 71.905 53.932 71.905 53.932s23.87-69.542 52.145-66.885c28.32 2.61 75.64 37.697 75.64 37.697S786.998 329.519 747.328 232.189zM510.814 954.878l-486.659-2.035c0 0 104.097-262.609 135.555-351.674 31.462-89.067 92.015-227.959 174.37-99.555 82.302 128.41 157.402 374.639 157.402 374.639L510.814 954.878zM313.637 582.083c-21.457-51.955-40.405-66.6-60.607-54.615-20.2 11.985-50.452 133.29-50.452 133.29s18.27-15.365 34.455-16.725c16.147-1.3 38.617 28.66 38.617 28.66s12.565-37.26 27.692-35.905c15.175 1.31 40.592 19.91 40.592 19.91S335.094 634.078 313.637 582.083z"
              fill="#0e932e" p-id="6032"></path>
          </svg>
          ️
        </div>
        <div class="nav-text">景点</div>
      </div>
      <div class="nav-item" @click="navigateTo('/pages/hotel/index')">
        <div class="nav-icon">
          <svg t="1774967533900" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg"
            p-id="8849" width="256" height="256">
            <path
              d="M513.024 510.0544m-453.2224 0a453.2224 453.2224 0 1 0 906.4448 0 453.2224 453.2224 0 1 0-906.4448 0Z"
              fill="#58A2F8" p-id="8850"></path>
            <path
              d="M513.024 56.832c-250.3168 0-453.2224 202.9056-453.2224 453.2224 0 99.7888 32.256 192 86.8864 266.8544 74.8544 54.6304 167.0656 86.8864 266.8544 86.8864 250.3168 0 453.2224-202.9056 453.2224-453.2224 0-99.7888-32.256-192-86.8864-266.8544C704.9728 89.088 612.7616 56.832 513.024 56.832z"
              fill="#57ADFA" p-id="8851"></path>
            <path
              d="M756.6848 303.5136c0-72.6016-17.152-141.2096-47.5136-202.0352-59.3408-28.5696-125.8496-44.5952-196.096-44.5952-250.3168 0-453.2224 202.9056-453.2224 453.2224 0 72.6016 17.152 141.2096 47.5136 202.0352 59.3408 28.5696 125.8496 44.5952 196.096 44.5952 250.2656 0 453.2224-202.9056 453.2224-453.2224z"
              fill="#55C2FD" p-id="8852"></path>
            <path
              d="M59.8016 510.0544c0 38.912 4.9664 76.6464 14.2336 112.64a443.5968 443.5968 0 0 0 114.688 15.0528c245.7088 0 444.928-199.2192 444.928-444.928 0-43.1616-6.2464-84.7872-17.7152-124.2112-33.024-7.68-67.4816-11.776-102.8608-11.776-250.368 0-453.2736 202.9056-453.2736 453.2224z"
              fill="#54D2FF" p-id="8853"></path>
            <path
              d="M712.704 583.68l49.2544-19.2512v158.5152h29.0816s20.3776 0.768 20.3776 20.9408v34.8672s0.512 20.48-20.9408 20.48h-264.8576l10.752-60.3648 130.0992-78.9504s15.104-3.7888 20.224-20.224c5.0688-16.4352 26.0096-56.0128 26.0096-56.0128z"
              fill="#BFE0FF" p-id="8854"></path>
            <path
              d="M346.7776 422.7584V363.8272c0-15.3088 12.3904-27.6992 27.6992-27.6992h58.9312l141.4144-141.4144h-235.52c-43.3664 0-78.5408 35.1744-78.5408 78.5408v235.4688l86.016-85.9648z"
              fill="#FFFFFF" p-id="8855"></path>
            <path
              d="M683.3664 194.7136h-108.5952L433.408 336.128h18.1248c15.3088 0 27.6992 12.3904 27.6992 27.6992v77.056c0 15.3088-12.3904 27.6992-27.6992 27.6992H374.4768c-15.3088 0-27.6992-12.3904-27.6992-27.6992v-18.1248l-86.016 86.016v214.1696h-28.4672c-11.5712 0-20.9408 9.3696-20.9408 20.9408v34.4576c0 11.5712 9.3696 20.9408 20.9408 20.9408h16.4352l138.3936-138.3936h-12.6464c-15.3088 0-27.6992-12.3904-27.6992-27.6992v-77.056c0-15.3088 12.3904-27.6992 27.6992-27.6992h77.056c15.3088 0 27.6992 12.3904 27.6992 27.6992v12.6464l100.5056-100.5056c-13.824-1.536-24.576-13.1584-24.576-27.392V363.8272c0-15.3088 12.3904-27.6992 27.6992-27.6992h77.056c14.2336 0 25.8048 10.8032 27.392 24.6272l74.752-74.752v-12.6976c-0.1024-43.4176-35.2768-78.592-78.6944-78.592z"
              fill="#EAF5FF" p-id="8856"></path>
            <path
              d="M687.2064 360.7552c0.1024 1.024 0.3072 2.048 0.3072 3.072v77.056c0 15.3088-12.3904 27.6992-27.6992 27.6992h-77.056c-1.0752 0-2.048-0.2048-3.072-0.3072l-100.5056 100.5056v64.4096c0 15.3088-12.3904 27.6992-27.6992 27.6992H387.1232L248.7296 799.232h278.4256l139.264-139.264c-2.0992 0.512-4.3008 0.8704-6.5536 0.8704h-77.056c-15.3088 0-27.6992-12.3904-27.6992-27.6992v-77.056c0-15.3088 12.3904-27.6992 27.6992-27.6992h77.056c15.3088 0 27.6992 12.3904 27.6992 27.6992v77.056c0 2.2528-0.3584 4.4544-0.8704 6.5536l75.264-75.264V286.0032l-74.752 74.752z"
              fill="#D6EBFF" p-id="8857"></path>
          </svg>
        </div>
        <div class="nav-text">酒店</div>
      </div>
      <div class="nav-item" @click="navigateTo('/pages/ticket/index')">
        <div class="nav-icon">
          <svg t="1774967621564" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg"
            p-id="10876" width="256" height="256">
            <path d="M512 512m-512 0a512 512 0 1 0 1024 0 512 512 0 1 0-1024 0Z" fill="#FFFFFF" p-id="10877"></path>
            <path
              d="M884.696 519.76c-41.32 9.536-82.576-16.24-92.112-57.552s16.24-82.576 57.56-92.112l-37.44-162.136a12.64 12.64 0 0 0-15.344-9.592L123.872 353.856a12.64 12.64 0 0 0-9.592 15.352l37.432 162.136c41.312-9.536 82.568 16.24 92.104 57.552 9.544 41.312-16.24 82.568-57.552 92.112l31.68 137.192a12.64 12.64 0 0 0 15.344 9.592l673.488-155.488a12.64 12.64 0 0 0 9.592-15.352l-31.68-137.192z"
              fill="#FF7400" p-id="10878"></path>
            <path
              d="M230.528 840.744c-4.856 0.296-9.288-1.144-13.312-4.32-6.352-2.64-10.128-8.336-11.752-15.344l-34.552-149.664 12.48-2.88c34.288-7.92 55.872-42.464 47.952-76.76-7.92-34.296-42.456-55.88-76.752-47.96l-12.48 2.88-40.304-174.608c-1.616-7.016-0.72-13.792 2.88-19.552 3.6-5.76 9.288-9.536 16.304-11.152L794.48 185.896c14.032-3.24 27.464 5.16 30.704 19.184l40.304 174.608-12.472 2.88c-34.296 7.92-55.88 42.464-47.96 76.76 3.784 16.368 13.616 30.52 28.184 40.288 14.4 8.992 31.424 11.632 48.576 7.68l12.472-2.88 34.552 149.656c3.24 14.032-5.16 27.464-19.184 30.704L236.16 840.264c-1.56 0.36-3.896 0.896-5.64 0.48zM201.44 689.816l28.976 125.504 673.488-155.488-28.976-125.496a82.96 82.96 0 0 1-54.864-13.608c-20.152-12.592-34.544-32.256-39.944-55.64-10.08-43.656 14.2-87.848 54.856-103.8l-34.728-150.448-673.488 155.488 34.736 150.44c43.528-3.48 84.72 25.6 94.8 69.248 10.08 43.656-14.2 87.848-54.856 103.8z"
              fill="#333333" p-id="10879"></path>
            <path
              d="M729.28 700.144l-11.512-49.888 24.944-5.76 11.52 49.888-24.944 5.76z m-17.272-74.832l-17.28-74.832 24.952-5.76 17.28 74.832-24.952 5.76z m-23.032-99.776l-17.28-74.832 24.944-5.76 17.28 74.832-24.944 5.76z m-23.04-99.776l-17.272-74.832 24.944-5.76 17.28 74.832-24.944 5.76z m-23.032-99.776l-17.28-74.832 24.952-5.76 17.28 74.832-24.952 5.76z"
              fill="#2C2C2C" p-id="10880"></path>
            <path
              d="M421.6 427.16l-3.424-14.808 16.368-3.776 3.416 14.8 91.2-21.048 3.24 14.032-91.2 21.056 2.88 12.472 70.936-16.376 15.12 65.472-10.92 2.52c15.776 7.856 28.24 15.648 37.24 22.608l-9.712 11.272c-4.976-3.776-9.952-7.552-15.712-11.152a4128.952 4128.952 0 0 1-165.192 45.52l-5.576-13.488c31-7.976 59.84-14.64 85.384-21.352l-3.24-14.032-70.936 16.376-15.12-65.472 70.944-16.376-2.88-12.48-91.2 21.056-3.24-14.024 91.616-22.8z m-25.776 147.992c-1.136 19.968-5.152 38.136-11.272 54.32l-15.224-5.504c7.072-15.6 10.664-32.024 11.744-48.696l14.752-0.12z m38.184-94.208l-3.416-14.8-55.344 12.768 3.416 14.816 55.344-12.784z m2.52 10.92l-55.344 12.776 3.424 14.808 55.344-12.776-3.424-14.808z m7.392 128.016c-13.256 3.056-20.928-1.736-23.808-14.208l-9-38.976 15.592-3.6 8.28 35.856c1.08 4.68 4.912 7.08 10.368 5.816l51.448-11.88c3.896-0.896 6.84-2.4 8.64-5.28 1.792-2.872 1.736-10.248-0.368-22.896l15.896 1.256c2.216 16.72 1.44 27.584-1.56 32.376-3.176 4.016-8.272 6.84-16.072 8.64l-59.416 12.896z m5.68-142.528l55.344-12.784-3.416-14.8-55.344 12.768 3.416 14.816z m41.56 94.672l-10.488 11.456c-8.216-7.136-19.728-14.328-33.16-22.72l10.672-10.68c13.432 8.4 24.944 15.592 32.976 21.944z m17.08-96.72l-55.344 12.776 3.424 14.808 55.344-12.776-3.424-14.808z m-2.928 33.52l4.736-4.376-51.448 11.872 3.24 14.032a7960.72 7960.72 0 0 0 54.8-15.12c-3.84-2.392-7.68-4.792-11.328-6.408z m65.6 63.68l-10.488 11.44c-11.64-11.264-25.968-23.552-43.776-36.688l10.672-10.672a526.592 526.592 0 0 1 43.6 35.92z"
              fill="#434343" p-id="10881"></path>
            <path
              d="M873.6 601.6c-42.4 0-76.8-34.4-76.8-76.8s34.4-76.8 76.8-76.8V281.6c0-7.2-5.6-12.8-12.8-12.8H169.6c-7.2 0-12.8 5.6-12.8 12.8V448c42.4 0 76.8 34.4 76.8 76.8s-34.4 76.8-76.8 76.8v140.8c0 7.2 5.6 12.8 12.8 12.8h691.2c7.2 0 12.8-5.6 12.8-12.8V601.6z"
              fill="#FFB900" p-id="10882"></path>
            <path
              d="M860.8 768H169.6c-14.4 0-25.6-11.2-25.6-25.6V588.8h12.8c35.2 0 64-28.8 64-64s-28.8-64-64-64H144V281.6c0-14.4 11.2-25.6 25.6-25.6h691.2c14.4 0 25.6 11.2 25.6 25.6v179.2h-12.8c-35.2 0-64 28.8-64 64s28.8 64 64 64h12.8v153.6c0 14.4-11.2 25.6-25.6 25.6zM169.6 613.6v128.8h691.2V613.6c-43.2-6.4-76.8-44-76.8-88.8s33.6-82.4 76.8-88.8V281.6H169.6v154.4c43.2 6.4 76.8 44 76.8 88.8s-33.6 82.4-76.8 88.8z"
              fill="#333333" p-id="10883"></path>
            <path
              d="M707.2 742.4h-25.6v-51.2h25.6v51.2z m0-76.8h-25.6v-76.8h25.6v76.8z m0-102.4h-25.6v-76.8h25.6v76.8z m0-102.4h-25.6V384h25.6v76.8z m0-102.4h-25.6v-76.8h25.6v76.8z"
              fill="#2C2C2C" p-id="10884"></path>
            <path
              d="M553.6 401.6V416h-69.6v18.4h52.8v51.2H365.6v-51.2h52.8V416h-69.6v-14.4h204.8z m-4 132v14.4h-88V584c0 12.8-7.2 19.2-20.8 19.2h-20.8l-3.2-14.4c6.4 0.8 12.8 0.8 18.4 0.8 6.4 0 9.6-3.2 9.6-8.8v-32H352.8v-14.4h196.8z m-130.4 32.8c-15.2 13.6-34.4 24.8-56.8 34.4l-9.6-12.8c22.4-9.6 40.8-20 56-32l10.4 10.4z m108.8-64v13.6H375.2v-13.6H528zM418.4 448h-36.8v24h36.8v-24z m50.4-32h-34.4v18.4h34.4V416z m0 32h-34.4v24h34.4v-24z m52 0h-36.8v24h36.8v-24zM552 589.6l-11.2 11.2c-12.8-11.2-30.4-22.4-52.8-35.2l11.2-10.4c20 12 38.4 23.2 52.8 34.4z"
              fill="#434343" p-id="10885"></path>
          </svg>
        </div>
        <div class="nav-text">票务</div>
      </div>
      <div class="nav-item" @click="navigateTo('/pages/food/index')">
        <div class="nav-icon">
          <svg t="1774967652713" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg"
            p-id="13396" width="256" height="256">
            <path
              d="M921.344 867.4816H101.0688c-21.248 0-38.4512-17.2032-38.4512-38.4512v-33.3312c0-21.248 17.2032-38.4512 38.4512-38.4512h820.2752c21.248 0 38.4512 17.2032 38.4512 38.4512v33.3312c0 21.248-17.2032 38.4512-38.4512 38.4512zM904.704 591.9744h-5.6832c-21.0944-156.8256-134.912-284.16-284.5696-324.9664 0-0.512 0.0512-0.9728 0.0512-1.4848 0-57.0368-46.2336-103.3216-103.3216-103.3216s-103.3216 46.2336-103.3216 103.3216c0 0.512 0.0512 0.9728 0.0512 1.4848-149.6576 40.8064-263.5264 168.1408-284.5696 324.9664h-5.6832c-30.4128 0-55.0912 24.6784-55.0912 55.0912 0 30.4128 24.6784 55.0912 55.0912 55.0912h786.944c30.4128 0 55.0912-24.6784 55.0912-55.0912 0.1024-30.4128-24.576-55.0912-54.9888-55.0912zM447.5392 436.7872c-3.84 0.5632-86.6816 13.8752-121.1904 96.4096a37.06368 37.06368 0 0 1-34.2528 22.784 37.12 37.12 0 0 1-34.2016-51.456c51.3536-122.7264 174.3872-140.5952 179.5584-141.312 20.224-2.7648 39.0144 11.52 41.728 31.8464a37.2224 37.2224 0 0 1-31.6416 41.728z"
              fill="#FF3369" p-id="13397"></path>
            <path
              d="M889.6 544.6656C854.1184 409.9072 748.6976 303.616 614.4 267.008c0-0.512 0.0512-0.9728 0.0512-1.4848 0-57.0368-46.2336-103.3216-103.3216-103.3216-57.0368 0-103.3216 46.2336-103.3216 103.3216 0 0.512 0.0512 0.9728 0.0512 1.4848-149.6576 40.8064-263.5264 168.1408-284.5696 324.9664h-5.6832c-30.4128 0-55.0912 24.6784-55.0912 55.0912s24.6784 55.0912 55.0912 55.0912h689.152c34.9184-47.7184 62.976-100.6592 82.8416-157.4912zM447.5392 436.7872c-3.84 0.5632-86.6816 13.8752-121.1904 96.4096a37.06368 37.06368 0 0 1-34.2528 22.784 37.12 37.12 0 0 1-34.2016-51.456c51.3536-122.7264 174.3872-140.5952 179.6096-141.312 20.224-2.7648 39.0144 11.52 41.728 31.8464 2.6112 20.224-11.52 38.8608-31.6928 41.728zM114.5856 757.2992c-28.7232 0-51.968 23.2448-51.968 51.968v6.2976c0 28.7232 23.2448 51.968 51.968 51.968h510.9248c50.5344-30.1056 96.3584-67.2768 136.0896-110.2336H114.5856z"
              fill="#FF4D7C" p-id="13398"></path>
            <path
              d="M726.4256 317.7472c-33.9968-22.4256-71.68-39.7824-111.9744-50.7392 0-0.512 0.0512-0.9728 0.0512-1.4848 0-57.0368-46.2336-103.3216-103.3216-103.3216-57.0368 0-103.3216 46.2336-103.3216 103.3216 0 0.512 0.0512 0.9728 0.0512 1.4848-149.6576 40.8064-263.5264 168.1408-284.5696 324.9664h-5.6832c-30.4128 0-55.0912 24.6784-55.0912 55.0912s24.6784 55.0912 55.0912 55.0912h231.7824c177.8688-62.464 317.952-205.0048 376.9856-384.4096z m-434.3808 238.2336a37.12 37.12 0 0 1-34.2016-51.456c51.3536-122.7264 174.3872-140.5952 179.6096-141.312 20.224-2.7648 39.0144 11.52 41.728 31.8464a37.0944 37.0944 0 0 1-31.6416 41.6768c-3.84 0.5632-86.6816 13.8752-121.1904 96.4096a37.15072 37.15072 0 0 1-34.304 22.8352z"
              fill="#FF5F89" p-id="13399"></path>
            <path
              d="M146.8928 501.5552c53.8624-13.9264 104.8576-35.0208 151.8592-62.1568 27.9552-31.232 60.4672-49.9712 86.9888-61.0304 67.8912-57.0368 123.136-128.512 161.024-209.7664-11.1104-4.096-23.04-6.4-35.584-6.4-57.0368 0-103.3216 46.2336-103.3216 103.3216 0 0.512 0.0512 0.9728 0.0512 1.4848-119.552 32.6144-216.2176 120.5248-261.0176 234.5472z"
              fill="#FF7196" p-id="13400"></path>
            <path
              d="M206.6432 757.2992H114.5856c-24.2688 0-44.5952 16.64-50.3296 39.168 51.3536-11.0592 100.1472-25.2416 142.3872-39.168z"
              fill="#FF5F89" p-id="13401"></path>
          </svg>
        </div>
        <div class="nav-text">美食</div>
      </div>
      <div class="nav-item" @click="navigateTo('/pages/product/index')">
        <div class="nav-icon">🛍️</div>
        <div class="nav-text">特产</div>
      </div>
      <div class="nav-item" @click="navigateTo('/pages/travelPlan/index')">
        <div class="nav-icon">📅</div>
        <div class="nav-text">出行计划</div>
      </div>
      <div class="nav-item" @click="navigateTo('/pages/groupTravel/index')">
        <div class="nav-icon">👥</div>
        <div class="nav-text">跟团游</div>
      </div>
      <div class="nav-item" @click="navigateTo('/pages/news/index')">
        <div class="nav-icon">📰</div>
        <div class="nav-text">新闻</div>
      </div>
    </div>

    <!-- 推荐景点 -->
    <div class="section">
      <div class="section-header">
        <h2>推荐景点</h2>
        <button class="more-btn" @click="navigateTo('/pages/attraction/index')">查看更多</button>
      </div>
      <div class="recommend-list">
        <div v-for="attraction in recommendAttractions" :key="attraction.id" class="glass-card attraction-item"
          @click="navigateTo(`/pages/attraction/detail?id=${attraction.id}`)">
          <img :src="attraction.coverImage" :alt="attraction.name" class="item-image">
          <div class="item-info">
            <h3>{{ attraction.name }}</h3>
            <p class="text-secondary">{{ attraction.city }}</p>
            <div class="rating">
              <span class="star">★</span>
              <span>{{ attraction.rating }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 推荐酒店 -->
    <div class="section">
      <div class="section-header">
        <h2>推荐酒店</h2>
        <button class="more-btn" @click="navigateTo('/pages/hotel/index')">查看更多</button>
      </div>
      <div class="recommend-list">
        <div v-for="hotel in recommendHotels" :key="hotel.id" class="glass-card hotel-item"
          @click="navigateTo(`/pages/hotel/detail?id=${hotel.id}`)">
          <img :src="hotel.coverImage" :alt="hotel.name" class="item-image">
          <div class="item-info">
            <h3>{{ hotel.name }}</h3>
            <p class="text-secondary">{{ hotel.city }}</p>
            <div class="price">¥{{ hotel.price }}/晚</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 推荐美食 -->
    <div class="section">
      <div class="section-header">
        <h2>推荐美食</h2>
        <button class="more-btn" @click="navigateTo('/pages/food/index')">查看更多</button>
      </div>
      <div class="recommend-list">
        <div v-for="food in recommendFoods" :key="food.id" class="glass-card food-item"
          @click="navigateTo(`/pages/food/detail?id=${food.id}`)">
          <img :src="food.coverImage" :alt="food.name" class="item-image">
          <div class="item-info">
            <h3>{{ food.name }}</h3>
            <p class="text-secondary">{{ food.city }} - {{ food.cuisine }}</p>
            <div class="rating">
              <span class="star">★</span>
              <span>{{ food.rating }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 热门新闻 -->
    <div class="section">
      <div class="section-header">
        <h2>热门新闻</h2>
        <button class="more-btn" @click="navigateTo('/pages/news/index')">查看更多</button>
      </div>
      <div class="news-list">
        <div v-for="news in hotNews" :key="news.id" class="glass-card news-item"
          @click="navigateTo(`/pages/news/detail?id=${news.id}`)">
          <h3>{{ news.title }}</h3>
          <p class="news-desc">{{ news.description }}</p>
          <div class="news-meta">
            <span class="news-date">{{ news.createTime }}</span>
            <span class="news-view">{{ news.viewCount }} 浏览</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { getHotAttractions } from '@/api/attraction'
import { getRecommendHotels } from '@/api/hotel'
import { getRecommendFoods } from '@/api/food'
import { getHotNews } from '@/api/news'
import carouselApi from '@/api/carousel'
import Carousel from '../carousel/Carousel.vue'

// 轮播图数据
const carouselList = ref([])



// 推荐数据
const recommendAttractions = ref([])
const recommendHotels = ref([])
const recommendFoods = ref([])
const hotNews = ref([])

// 获取推荐景点
const getRecommendAttractions = async () => {
  try {
    const response = await getHotAttractions(4)
    recommendAttractions.value = response.data
  } catch (error) {
    console.error('获取推荐景点失败:', error)
  }
}

// 获取推荐酒店
const getRecommendHotelsList = async () => {
  try {
    const response = await getRecommendHotels('', 4)
    recommendHotels.value = response.data
  } catch (error) {
    console.error('获取推荐酒店失败:', error)
  }
}

// 获取推荐美食
const getRecommendFoodsList = async () => {
  try {
    const response = await getRecommendFoods('', 4)
    recommendFoods.value = response.data
  } catch (error) {
    console.error('获取推荐美食失败:', error)
  }
}

// 获取热门新闻
const getHotNewsList = async () => {
  try {
    const response = await getHotNews({ limit: 4 })
    hotNews.value = response.data
  } catch (error) {
    console.error('获取热门新闻失败:', error)
  }
}

// 获取轮播图数据
const getCarouselData = async () => {
  try {
    const response = await carouselApi.getHomeTopCarousels()
    carouselList.value = response.data
  } catch (error) {
    console.error('获取轮播图数据失败:', error)
  }
}

// 导航到指定页面
const navigateTo = (url) => {
  // 提取页面路径（去除参数部分）
  const pagePath = url.split('?')[0];
  
  // tabbar 页面列表
  const tabbarPages = [
    '/pages/index/index',
    '/pages/discover/index',
    '/pages/community/index',
    '/pages/news/index',
    '/pages/profile/index'
  ];
  
  // 检查是否为 tabbar 页面
  if (tabbarPages.includes(pagePath)) {
    // 使用 switchTab 跳转到 tabbar 页面
    uni.switchTab({
      url: pagePath
    });
  } else {
    // 使用 navigateTo 跳转到非 tabbar 页面
    uni.navigateTo({
      url
    });
  }
}

// 页面加载时获取数据
onMounted(() => {
  getCarouselData()
  getRecommendAttractions()
  getRecommendHotelsList()
  getRecommendFoodsList()
  getHotNewsList()
})

// 页面卸载时清理
onUnmounted(() => {
})
</script>

<style scoped>
/* CSS 变量定义 */
:root {
  --background-color: #f5f5f5;
  --text-color: #333333;
  --primary-color: #007aff;
  --text-secondary-color: #666666;
}

.page-container {
  padding: 8px;
  min-height: 100vh;
  background-color: var(--background-color);
}

/* 动画效果 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.nav-container {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  padding: 20px;
  margin-bottom: 24px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(31, 38, 135, 0.37);
  border: 1px solid rgba(255, 255, 255, 0.18);
}

.nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px 12px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.nav-item:hover {
  transform: translateY(-4px);
  background: rgba(255, 255, 255, 0.1);
  box-shadow: 0 4px 16px rgba(30, 136, 229, 0.3);
  border-color: rgba(30, 136, 229, 0.5);
}

.nav-icon {
  font-size: 32px;
  margin-bottom: 12px;
  transition: all 0.3s ease;
}

.nav-item:hover .nav-icon {
  transform: scale(1.1);
}

.nav-icon svg {
  width: 32px;
  height: 32px;
}

.nav-text {
  font-size: 14px;
  color: var(--text-color);
  font-weight: 500;
  transition: all 0.3s ease;
}

.nav-item:hover .nav-text {
  color: var(--primary-color);
}

.section {
  margin-bottom: 30px;
  box-shadow: 0 8px 32px rgba(31, 38, 135, 0.37);
  padding: 8px;
  border-radius: 16px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-header h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: var(--text-color);
  display: flex;
  align-items: center;
  gap: 8px;
}

.section-header h2::before {
  content: '';
  width: 4px;
  height: 20px;
  background: linear-gradient(to bottom, var(--primary-color), #0056b3);
  border-radius: 2px;
}

.more-btn {
  background: rgba(30, 136, 229, 0.1);
  border: 1px solid rgba(30, 136, 229, 0.3);
  font-size: 12px;
  color: var(--primary-color);
  cursor: pointer;
  border-radius: 20px;
  transition: all 0.3s ease;
  font-weight: 500;
}

.more-btn:hover {
  background-color: var(--primary-color);
  color: white;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(30, 136, 229, 0.4);
}

.recommend-list {
  display: flex;
  overflow-x: auto;
  padding-bottom: 16px;
  -webkit-overflow-scrolling: touch;
  gap: 8px;
  margin-top: 16px;
}

.recommend-list .glass-card {
  flex: 0 0 220px;
  padding: 0;
  cursor: pointer;
  transition: all 0.3s ease;
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(8px);
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.1);
  overflow: hidden;
}

.recommend-list .glass-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(30, 136, 229, 0.3);
  border-color: rgba(30, 136, 229, 0.3);
}

.item-image {
  width: 100%;
  height: 140px;
  border-radius: 16px 16px 0 0;
  object-fit: cover;
  margin-bottom: 0;
  transition: all 0.3s ease;
}

.recommend-list .glass-card:hover .item-image {
  transform: scale(1.05);
}

.item-info {
  padding: 16px;
}

.item-info h3 {
  margin: 0 0 10px 0;
  font-size: 16px;
  font-weight: 600;
  color: var(--text-color);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  line-height: 1.3;
}

.text-secondary {
  margin: 0 0 12px 0;
  font-size: 14px;
  color: var(--text-secondary-color);
  line-height: 1.4;
}

.rating {
  display: flex;
  align-items: center;
  margin-top: 8px;
  padding-top: 8px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.star {
  color: #FF9800;
  margin-right: 6px;
  font-size: 16px;
}

.rating span {
  font-size: 14px;
  font-weight: 500;
  color: #FF9800;
}

.price {
  font-size: 18px;
  font-weight: 700;
  color: #F44336;
  margin-top: 8px;
  padding-top: 8px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.price::after {
  content: '/晚';
  font-size: 12px;
  font-weight: 500;
  color: var(--text-secondary-color);
}

.news-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.news-item {
  padding: 20px;
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(8px);
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.news-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(30, 136, 229, 0.3);
  background: rgba(255, 255, 255, 0.1);
  border-color: rgba(30, 136, 229, 0.3);
}

.news-item h3 {
  margin: 0 0 12px 0;
  font-size: 18px;
  font-weight: 600;
  color: var(--text-color);
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.4;
}

.news-desc {
  margin: 0 0 16px 0;
  font-size: 14px;
  color: var(--text-secondary-color);
  line-height: 1.5;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.news-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
  color: var(--text-secondary-color);
  padding-top: 12px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.news-date {
  display: flex;
  align-items: center;
  gap: 4px;
}

.news-view {
  display: flex;
  align-items: center;
  gap: 4px;
  background: rgba(30, 136, 229, 0.1);
  padding: 4px 12px;
  border-radius: 12px;
  color: var(--primary-color);
  font-weight: 500;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .swiper {
    height: 150px;
  }

  .nav-container {
    grid-template-columns: repeat(4, 1fr);
    gap: 12px;
    padding: 16px;
  }

  .recommend-list .glass-card {
    flex: 0 0 150px;
  }

  .item-image {
    height: 100px;
  }
}
</style>